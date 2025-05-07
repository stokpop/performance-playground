package nl.stokpop.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 3, time = 18)
@Fork(1)
@State(Scope.Benchmark)
public class WebClientBenchmark {

    private WebClientCaller webClientCaller;
    private WireMockServer wireMockServer;
    private String authToken;

    @Setup(Level.Trial)
    public void setup() throws Exception {

        wireMockServer = WebClientWiremockServer.createWiremockServer();

        wireMockServer.start();

        // Create WebClient with SSL support
        String url = "https://localhost:" + WebClientWiremockServer.HTTPS_PORT;

        WebClient webClient = WebClientConfig.createWebClient(url);

        // Create WebClient builder with SSL support
        WebClient.Builder webClientBuilder = WebClientConfig.createWebClientBuilder(url);

        webClientCaller = new WebClientCaller(webClient, webClientBuilder);
        
        // Generate a fixed auth token for benchmarking
        authToken = UUID.randomUUID().toString();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        wireMockServer.stop();
    }

    @Benchmark
    public String benchmarkWebClient() {
        return webClientCaller.callWebClient(authToken);
    }

    @Benchmark
    public String benchmarkWebClientBuilder() {
        return webClientCaller.callWebClientBuilder(authToken);
    }

    // Main method to run the benchmark
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(WebClientBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}