package nl.stokpop.jmh;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsJwt {

    private final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.";

    private final JwtParser PARSER = Jwts.parser();

    @Benchmark
    public void parseJwtNewParser(Blackhole blackhole) {
        blackhole.consume(Jwts.parser().parse(token));
    }

    @Benchmark
    public void parseJwtReuseParser(Blackhole blackhole) {
        blackhole.consume(PARSER.parse(token));
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
            .forks(4)
            .warmupIterations(10)
            .measurementIterations(10)
            .verbosity(VerboseMode.EXTRA)
            .include("Jwt")
            .build();

        new Runner(options).run();
    }
}
