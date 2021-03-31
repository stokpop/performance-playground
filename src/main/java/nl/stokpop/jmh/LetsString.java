package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsString {

    private String[] ids = { "1", "2", "3"};
    // use char to prevent negatives
    private char i;

    @Benchmark
    public void plainStringConcat(Blackhole blackhole) {
        blackhole.consume("test." + ids[i++ % 3]);
    }

    @Benchmark
    public void plainStringFormat(Blackhole blackhole) {
        blackhole.consume(String.format("test.%s", ids[i++ % 3]));
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("plainString")
                .build();

        new Runner(options).run();
    }
}
