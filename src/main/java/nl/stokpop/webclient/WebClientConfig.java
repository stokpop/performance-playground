package nl.stokpop.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;

public class WebClientConfig {
    static WebClient.Builder createWebClientBuilder(String baseUrl) {

        HttpClient httpClient = createHttpClient();

        WebClient.Builder webClientBuilder = WebClient.builder();
        webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
        webClientBuilder.baseUrl(baseUrl);
        webClientBuilder.defaultHeader("Content-Type", "application/json");
        webClientBuilder.defaultHeader("Accept", "application/json");
        return webClientBuilder;
    }

    public static WebClient createWebClient(String baseUrl) {

        HttpClient httpClient = createHttpClient();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @NotNull
    private static HttpClient createHttpClient() {
        ConnectionProvider provider = ConnectionProvider.builder("mtls-connection-pool")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(10))
                .maxLifeTime(Duration.ofSeconds(30))
                .pendingAcquireTimeout(Duration.ofMillis(600))
                .evictInBackground(Duration.ofSeconds(5))
                .disposeTimeout(Duration.ofSeconds(5))  // Time allowed for proper disposal

                .build();

        return HttpClient.create(provider)
                .option(ChannelOption.SO_LINGER, 1) // Default is -1, which means no linger, now 1 second
                .option(ChannelOption.ALLOW_HALF_CLOSURE, true) // Support half-closed connections
                .secure(sslSpec -> {
                    try {
                        sslSpec.sslContext(createSslContext());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static SslContext createSslContext() throws Exception {
        ClassLoader classLoader = WebClientExecutor.class.getClassLoader();

        // Load client keystore (for client authentication)
        KeyStore keyStore = KeyStore.getInstance("JKS");
        char[] keyStorePassword = "changeit".toCharArray();
        try (InputStream is = classLoader.getResourceAsStream("client.jks")) {
            keyStore.load(is, keyStorePassword);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword);

        // Load client truststore (to trust the server)
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream is = classLoader.getResourceAsStream("client-truststore.jks")) {
            trustStore.load(is, keyStorePassword);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Create SSL context with key manager and trust manager
        return SslContextBuilder.forClient()
                //.trustManager(InsecureTrustManagerFactory.INSTANCE)
                .keyManager(kmf)
                .trustManager(tmf)
                .sessionCacheSize(0)
                .sessionTimeout(600)
                .protocols("TLSv1.2", "TLSv1.3")
                .build();
    }
}
