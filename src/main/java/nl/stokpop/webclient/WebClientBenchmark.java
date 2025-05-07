package nl.stokpop.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
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

        // Create a custom connection provider with appropriate pool settings
        ConnectionProvider provider = ConnectionProvider.builder("tls-connection-pool")
                .maxConnections(500)  // Default is unbounded
                .maxIdleTime(Duration.ofSeconds(20))  // Default is 20s, but explicitly setting
                .build();


        HttpClient httpClient1 = HttpClient.create(provider)
                .secure(spec -> {
                    try {

                        // Load keystore
                        KeyStore keyStore = KeyStore.getInstance("JKS");
                        keyStore.load(
                                new ClassPathResource("client.jks").getInputStream(),
                                "changeit".toCharArray()
                        );

                        // Load truststore
                        KeyStore trustStore = KeyStore.getInstance("JKS");
                        trustStore.load(
                                new ClassPathResource("client-truststore.jks").getInputStream(),
                                "changeit".toCharArray()
                        );

                        // Setup key manager factory
                        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        keyManagerFactory.init(keyStore, "changeit".toCharArray());

                        // Setup trust manager factory
                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        trustManagerFactory.init(trustStore);

                        SslContext sslContext = SslContextBuilder.forClient()
                                .keyManager(keyManagerFactory)
                                .trustManager(trustManagerFactory)
                                .sessionCacheSize(4096)
                                .sessionTimeout(60)
                                .build();
                        spec.sslContext(sslContext);
                    } catch (javax.net.ssl.SSLException e) {
                        throw new RuntimeException("Failed to build SSL context", e);
                    } catch (IOException
                             | CertificateException
                             | KeyStoreException
                             | NoSuchAlgorithmException
                             | UnrecoverableKeyException e) {
                        throw new RuntimeException("Failed to create SSL context", e);
                    }
                });

        // Create WebClient with SSL support
        String url = "https://localhost:" + WebClientWiremockServer.HTTPS_PORT;

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient1))
                .baseUrl(url)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();

        HttpClient httpClient2 = HttpClient.create()
                .secure(spec -> {
                    try {
                        spec.sslContext(
                                SslContextBuilder.forClient()
                                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                        .build()
                        );
                    } catch (javax.net.ssl.SSLException e) {
                        throw new RuntimeException("Failed to build SSL context", e);
                    }
                });

        // Create WebClient builder with SSL support
        WebClient.Builder webClientBuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient2))
                .baseUrl(url);

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