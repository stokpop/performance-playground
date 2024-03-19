package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.*;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsStreamToCollection {

    //Benchmark                                                     Mode  Cnt       Score      Error  Units
    //LetsStreamToCollection.generateCollectionFromStream          thrpt    5  225262,107 ± 6830,946  ops/s
    //LetsStreamToCollection.generateCollectionFromStreamInStream  thrpt    5  183440,867 ± 9563,691  ops/s
    //LetsStreamToCollection.generateCollectionWithFlatMap         thrpt    5  213506,640 ± 6483,180  ops/s

    private final static String[] numbers = { "", "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen",
            "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};

    private final static List<String> tens = List.of("", "", "twenty", "thirty", "forty", "fifty",
            "sixty", "seventy", "eighty", "ninety");

    @Benchmark
    public void generateCollectionFromStream(Blackhole blackhole) {
        Set<String> allNumbers = new HashSet<>();
        tens.stream()
                .filter(ten -> !ten.isEmpty())
                .forEach(ten ->
                        Arrays.stream(numbers)
                                .filter(number -> !number.isEmpty())
                                .map(number -> ten + " " + number)
                                .forEach(allNumbers::add)
                );
        blackhole.consume(allNumbers);
    }

    @Benchmark
    public void generateCollectionFromStreamInStream(Blackhole blackhole) {
        Set<String> allNumbers = new HashSet<>();
        tens.stream()
                .filter(ten -> !ten.isEmpty())
                .forEach(ten ->
                        allNumbers.addAll(Arrays.stream(numbers)
                                .filter(number -> !number.isEmpty())
                                .map(number -> ten + " " + number)
                                .collect(Collectors.toList())
                        )
                );
        blackhole.consume(allNumbers);
    }

    @Benchmark
    public void generateCollectionWithFlatMap(Blackhole blackhole) {
        Set<String> allNumbers = tens.stream()
                .filter(ten -> !ten.isEmpty())
                .flatMap(ten ->
                        Arrays.stream(numbers)
                                .filter(number -> !number.isEmpty())
                                .map(number -> ten + " " + number)
                )
                .collect(Collectors.toSet());
        blackhole.consume(allNumbers);
    }

    public static void main(String[] args) throws Exception {

        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(5)
                .verbosity(VerboseMode.EXTRA)
                .include("generateCollection")
                .build();

        new Runner(options).run();
    }
}
