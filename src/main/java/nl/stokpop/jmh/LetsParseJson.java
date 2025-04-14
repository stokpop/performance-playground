package nl.stokpop.jmh;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
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


// Benchmark                                  Mode  Cnt        Score        Error  Units
//LetsParseGson.parseJsonNewGson            thrpt    4    61840,160 ±   1591,536  ops/s
//LetsParseGson.parseJsonReuseGson          thrpt    4   662519,182 ±   9743,759  ops/s
//LetsParseJson.parseJsonNameOnlyObject     thrpt    4  1342566,661 ± 159923,749  ops/s
//LetsParseJson.parseJsonNameOnlyTree       thrpt    4   771198,281 ±   8476,414  ops/s
//LetsParseJson.parseJsonNewObjectMapper    thrpt    4    31847,975 ±    461,379  ops/s
//LetsParseJson.parseJsonReuseObjectMapper  thrpt    4   846913,636 ±  39902,903  ops/s
//LetsParseJson.parseJsonReuseObjectReader  thrpt    4   872881,838 ±  42258,373  ops/s


@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsParseJson {

    private String robotJson = "{\"name\":\"Robby\",\"head\":{\"name\":\"head\",\"eyes\":[{\"name\":\"left\",\"type\":\"Camera\"},{\"name\":\"right\",\"type\":\"Zoom\"}]},\"arms\":[{\"name\":\"arm1\",\"type\":\"Static\",\"length\":100},{\"name\":\"arm2\",\"type\":\"Telescopic\",\"length\":390}],\"legs\":[{\"name\":\"leg1\",\"type\":\"Human\"},{\"name\":\"leg2\",\"type\":\"Spider\"}]}";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ObjectReader robotReader = objectMapper.readerFor(Robot.class);

    private static final ObjectMapper objectMapperLite = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final ObjectReader robotNameOnlyReader = objectMapperLite.readerFor(RobotNameOnly.class);

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
        Robot robot = robotReader.readValue(robotJson);
        blackhole.consume(robot);
    }

    @Benchmark
    public void parseJsonNameOnlyObject(Blackhole blackhole) throws IOException {
        RobotNameOnly robotNameOnly = robotNameOnlyReader.readValue(robotJson);
        blackhole.consume(robotNameOnly.getName());
    }

    @Benchmark
    public void parseJsonNameOnlyTree(Blackhole blackhole) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(robotJson);
        blackhole.consume(jsonNode.get("name").asText());
    }


    private static class RobotNameOnly {
        private String name;

        // Default constructor required by Jackson
        public RobotNameOnly() {
        }

        public RobotNameOnly(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("parseJson")
                .build();

        new Runner(options).run();
    }
}
