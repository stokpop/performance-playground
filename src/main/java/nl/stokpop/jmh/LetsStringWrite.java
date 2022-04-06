package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.StringWriter;

/**
 * Investigate the impact of pre-sizing a StringWriter: when does it pay-off?
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@CompilerControl(CompilerControl.Mode.EXCLUDE)
public class LetsStringWrite {

    /* make members public and non static to avoid optimizations (if that makes sense?) */
    
    @Param({"16", "256", "1024", "2048", "10240"})
    public int initialSize;

    @Param({"12000", "24000", "180000"})
    public int messageSize;

    public String ALPHABET = "abcde12345";

    public char[] message;

    @Setup(Level.Iteration)
    public void setUp() {
        message = ALPHABET.repeat(messageSize/ALPHABET.length()).toCharArray();
    }


    @Benchmark
    public void stringWriterWithInitialSize(Blackhole blackhole) {
        StringWriter stringWriter = new StringWriter(initialSize);
        for (char c : message) {
            stringWriter.write(c);
        }
        blackhole.consume(stringWriter);
    }

    @Benchmark
    public void stringWriterWithMatchingInitialSize(Blackhole blackhole) {
        StringWriter stringWriter = new StringWriter(messageSize + 2);
        for (char c : message) {
            stringWriter.write(c);
        }
        blackhole.consume(stringWriter);
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .threads(1)
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(3)
                .verbosity(VerboseMode.EXTRA)
                .measurementTime(TimeValue.seconds(10))
                .addProfiler("gc")
                .include("stringWriter")
                .build();

        new Runner(options).run();
    }
}
