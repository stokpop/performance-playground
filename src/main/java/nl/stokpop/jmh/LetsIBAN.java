package nl.stokpop.jmh;

import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.math.BigInteger;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsIBAN {

    private static final int IBANNUMBER_MIN_SIZE = 15;
    private static final int IBANNUMBER_MAX_SIZE = 34;
    private static final int INITIAL_CHARACTER_SIZE = 4;
    private static final BigInteger IBANNUMBER_MAGIC_NUMBER = BigInteger.valueOf(97);

    public List<String> ibans;

    @Setup(Level.Trial)
    public void setUp() {
        // from https://www.iban.com/testibans
        ibans = List.of("GB33BUKB20201555555555", "GB94BARC10201530093459", "GB94BARC20201530093459", "GB96BARC202015300934591", "GB02BARC20201530093451", "GB68CITI18500483515538", "GB24BARC20201630093459");
    }

    public boolean isValidIban(String accountNumber) {
        String newAccountNumber = accountNumber.trim();

        int length = newAccountNumber.length();

        if (length < IBANNUMBER_MIN_SIZE || length > IBANNUMBER_MAX_SIZE) {
            return false;
        }

        newAccountNumber = newAccountNumber.substring(INITIAL_CHARACTER_SIZE) + newAccountNumber.substring(0, INITIAL_CHARACTER_SIZE);

        StringBuilder numericAccountNumber = new StringBuilder();
        for (int i = 0; i < length; i++) {
            numericAccountNumber.append(Character.getNumericValue(newAccountNumber.charAt(i)));
        }

        BigInteger ibanNumber = new BigInteger(numericAccountNumber.toString());
        return ibanNumber.mod(IBANNUMBER_MAGIC_NUMBER).intValue() == 1;
    }

    @Benchmark
    public void checkisValidIBAN(Blackhole blackhole) {
        for (String iban: ibans) {
            blackhole.consume(isValidIban(iban));
        }
    }

    @Benchmark
    public void checkisValidIBAN4j(Blackhole blackhole) {
        for (String iban: ibans) {
            try {
                IbanUtil.validate(iban);
            } catch (InvalidCheckDigitException | IbanFormatException e) {
                blackhole.consume(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
            .forks(1)
            .warmupIterations(4)
            .measurementIterations(4)
            .verbosity(VerboseMode.EXTRA)
            .include("IBAN")
            .build();

        new Runner(options).run();
    }
}
