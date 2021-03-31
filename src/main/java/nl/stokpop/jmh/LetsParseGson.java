package nl.stokpop.jmh;

import com.google.gson.Gson;
import nl.stokpop.robot.domain.Robot;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsParseGson {

    public String robotJson = "{\"name\":\"Robby\",\"head\":{\"name\":\"head\",\"eyes\":[{\"name\":\"left\",\"type\":\"Camera\"},{\"name\":\"right\",\"type\":\"Zoom\"}]},\"arms\":[{\"name\":\"arm1\",\"type\":\"Static\",\"length\":100},{\"name\":\"arm2\",\"type\":\"Telescopic\",\"length\":390}],\"legs\":[{\"name\":\"leg1\",\"type\":\"Human\"},{\"name\":\"leg2\",\"type\":\"Spider\"}]}";

    private static final Gson gson = new Gson();

    @Benchmark
    public void parseJsonNewGson(Blackhole blackhole) {
        Gson gson = new Gson();
        blackhole.consume(gson.fromJson(robotJson, Robot.class));
    }

    @Benchmark
    public void parseJsonReuseGson(Blackhole blackhole) {
        blackhole.consume(gson.fromJson(robotJson, Robot.class));
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("Gson")
                .build();

        new Runner(options).run();
    }
}
