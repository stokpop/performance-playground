package nl.stokpop.jmh;

import nl.stokpop.streams.EnumExists;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsStreamOneTwoThree {

    // Run on Mac M1 Max 28-10-2025 with java 21.0.6-tem
    // Benchmark                               Mode  Cnt          Score         Error  Units
    //LetsStreamOneTwoThree.oneTwoThree      thrpt    4   66858004,182 ±  746421,340  ops/s
    //LetsStreamOneTwoThree.oneTwoThreeFast  thrpt    4  234463387,806 ± 5773369,606  ops/s

    @Benchmark
    public void oneTwoThree(Blackhole blackhole) {
        String value = "TWO";
        blackhole.consume(EnumExists.OneTwoThree.isOneOfEnum(value));
    }

    @Benchmark
    public void oneTwoThreeFast(Blackhole blackhole) {
        String value = "TWO";
        blackhole.consume(EnumExists.OneTwoThree.isOneOfEnumFast(value));
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .threads(1)
                .warmupIterations(2)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("LetsStreamOneTwoThree.one")
                .build();

        new Runner(options).run();
    }
}
