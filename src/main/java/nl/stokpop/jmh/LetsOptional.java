package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Optional;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsOptional {

    // Benchmark                               Mode  Cnt          Score   Error  Units
    // LetsOptional.optionalForNullCheck      thrpt       207192047,370          ops/s
    // LetsOptional.optionalRegularNullCheck  thrpt       323158816,541          ops/s

    private String[] ids = { "1", "2", "3", null};
    // use char to prevent negatives
    private char i;

    @Benchmark
    public void optionalForNullCheck(Blackhole blackhole) {
        blackhole.consume(Optional.ofNullable(ids[i++ % 4]).map(String::hashCode).orElse(null));
    }

    @Benchmark
    public void optionalRegularNullCheck(Blackhole blackhole) {
        String id = ids[i++ % 4];
        blackhole.consume(id == null ? null : id.hashCode());
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(1)
                .verbosity(VerboseMode.EXTRA)
                .include("optional")
                .build();

        new Runner(options).run();
    }
}
