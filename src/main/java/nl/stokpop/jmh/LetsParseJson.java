package nl.stokpop.jmh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import nl.stokpop.robot.domain.Robot;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsParseJson {

    private String robotJson = "{\"name\":\"Robby\",\"head\":{\"name\":\"head\",\"eyes\":[{\"name\":\"left\",\"type\":\"Camera\"},{\"name\":\"right\",\"type\":\"Zoom\"}]},\"arms\":[{\"name\":\"arm1\",\"type\":\"Static\",\"length\":100},{\"name\":\"arm2\",\"type\":\"Telescopic\",\"length\":390}],\"legs\":[{\"name\":\"leg1\",\"type\":\"Human\"},{\"name\":\"leg2\",\"type\":\"Spider\"}]}";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ObjectReader robotReader = objectMapper.readerFor(Robot.class);

    @Benchmark
    public void parseJsonNewObjectMapper(Blackhole blackhole) throws IOException {
        ObjectMapper localObjectMapper = new ObjectMapper();
        blackhole.consume(localObjectMapper.readValue(robotJson, Robot.class));
    }

    @Benchmark
    public void parseJsonReuseObjectMapper(Blackhole blackhole) throws IOException {
        blackhole.consume(objectMapper.readValue(robotJson, Robot.class));
    }

    @Benchmark
    public void parseJsonReuseObjectReader(Blackhole blackhole) throws IOException {
        blackhole.consume(robotReader.readValue(robotJson));
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("Json")
                .build();

        new Runner(options).run();
    }
}
