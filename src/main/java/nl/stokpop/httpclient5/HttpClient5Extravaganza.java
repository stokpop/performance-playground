package nl.stokpop.httpclient5;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
public class HttpClient5Extravaganza {

    private boolean mtlsEnabled = false;

    @Bean
    SslBundles sslBundles() {
        return null;
    }

    @Bean
    public CloseableHttpClient httpClient5(SslBundles sslBundles) {

        // Define defaults that do NOT include TTL; TTL is set on the connection manager itself.
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setTimeToLive(TimeValue.ofSeconds(60)) // TTL for persistent connections
                .setConnectTimeout(Timeout.ofMilliseconds(300))
                .build();

        // Build connection manager with optional TLS strategy from SSL bundle
        PoolingHttpClientConnectionManager connectionManager;
        if (mtlsEnabled) {
            log.info("mTLS is enabled: configuring Apache HttpClient with SSLContext from Spring SSL bundle");
            connectionManager = createConnectionManagerWithMtls(sslBundles, connectionConfig);
        } else {
            log.info("mTLS is disabled: using default Apache HttpClient5 connection manager (no client certificates)");
            connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setDefaultConnectionConfig(connectionConfig)
                    //.setConnectionTimeToLive(TimeValue.ofSeconds(60)) // -> DEPRECATED
                    .build();
        }

        // Tune pool sizes to ensure reuse under concurrency
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(50);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(100))
                //.setConnectTimeout(Timeout.ofMilliseconds(100)) // -> DEPRECATED
                .setResponseTimeout(Timeout.ofMilliseconds(1200))
                .build();

        var httpClientBuilder = HttpClients.custom()
                .disableAutomaticRetries()
                // .disableConnectionState() // needed for mTLS connection reuse! // BAD: call missing
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                // Encourage persistent connections even when server omits keep-alive headers
                .setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(60))
                // Be explicit about intent to keep the connection alive
                .setDefaultHeaders(List.of(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive")));

        return httpClientBuilder.build();
    }

    @Bean
    public CloseableHttpClient httpClient5Minimal(SslBundles sslBundles) {

        // Define defaults that do NOT include TTL; TTL is set on the connection manager itself.
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                // .setTimeToLive(TimeValue.ofSeconds(60)) // TTL for persistent connections // BAD: call missing
                // .setConnectTimeout(Timeout.ofMilliseconds(300)) // BAD: call missing
                .build();

        // Build connection manager with optional TLS strategy from SSL bundle
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setDefaultConnectionConfig(connectionConfig)
                    //.setConnectionTimeToLive(TimeValue.ofSeconds(60)) // -> DEPRECATED
                    .build();

        // Tune pool sizes to ensure reuse under concurrency
        // connectionManager.setMaxTotal(100); // BAD: call missing
        // connectionManager.setDefaultMaxPerRoute(50); // BAD: call missing

        RequestConfig requestConfig = RequestConfig.custom()
                //.setConnectionRequestTimeout(Timeout.ofMilliseconds(100)) // BAD: call missing
                .setConnectTimeout(Timeout.ofMilliseconds(100)) // -> DEPRECATED
                //.setResponseTimeout(Timeout.ofMilliseconds(1200)) // BAD: call missing
                .build();

        var httpClientBuilder = HttpClients.custom()
                //.disableAutomaticRetries() // BAD: call missing
                //.disableConnectionState() // needed for mTLS connection reuse! // BAD: call missing
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .setConnectionManager(connectionManager)
                //.setDefaultRequestConfig(requestConfig) // BAD: call missing
                // Encourage persistent connections even when server omits keep-alive headers
                .setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(60))
                // Be explicit about intent to keep the connection alive
                .setDefaultHeaders(List.of(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive")));

        return httpClientBuilder.build();
    }


    private static PoolingHttpClientConnectionManager createConnectionManagerWithMtls(SslBundles sslBundles, ConnectionConfig connectionConfig) {
        PoolingHttpClientConnectionManager connectionManager;
        try {
            SSLContext sslContext;
            try {
                // Try to obtain SSL context from Spring SSL bundle
                SslBundle bundle = sslBundles.getBundle("mtls-client");
                sslContext = bundle.createSslContext();
            } catch (org.springframework.boot.ssl.NoSuchSslBundleException nsbe) {
                String msg = "SSL bundle 'mtls-client' not found.";
                log.error(msg);
                throw new IllegalStateException(msg, nsbe);
            }

            sslContext.getClientSessionContext().setSessionCacheSize(1000);
            sslContext.getClientSessionContext().setSessionTimeout(Duration.ofHours(2).toSecondsPart());
            TlsSocketStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);

            connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setDefaultConnectionConfig(connectionConfig)
                    .setTlsSocketStrategy(tlsStrategy)
                    .build();

            return connectionManager;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create SSL-enabled Apache HttpClient; SSL bundle missing.", e);
        }
    }

    @Bean
    public CloseableHttpClient httpClient5minimalWithMethodCall(SslBundles sslBundles) {

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setTimeToLive(TimeValue.ofSeconds(60)) // TTL for persistent connections
                .setConnectTimeout(Timeout.ofMilliseconds(300))
                .build();

        // Build connection manager with TLS strategy from SSL bundle
        PoolingHttpClientConnectionManager connectionManager = createConnectionManagerWithMtlsMinimal(sslBundles, connectionConfig);

        // Tune pool sizes to ensure reuse under concurrency
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(50);

        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    private static PoolingHttpClientConnectionManager createConnectionManagerWithMtlsMinimal(SslBundles sslBundles, ConnectionConfig connectionConfig) {
        PoolingHttpClientConnectionManager connectionManager;

        // skipped all mTLS code in the minimal example

        connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                //.setTlsSocketStrategy(tlsStrategy)
                .build();

            return connectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient5minimalOtherWithMethodCall(SslBundles sslBundles) {

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setTimeToLive(TimeValue.ofSeconds(60)) // TTL for persistent connections
                .setConnectTimeout(Timeout.ofMilliseconds(300))
                .build();

        // Build connection manager with TLS strategy from SSL bundle
        PoolingHttpClientConnectionManager connectionManager = createConnectionManagerWithMtlsMinimalOther(sslBundles, connectionConfig);

        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    private static PoolingHttpClientConnectionManager createConnectionManagerWithMtlsMinimalOther(SslBundles sslBundles, ConnectionConfig connectionConfig) {
        PoolingHttpClientConnectionManager connectionManager;

        // skipped all mTLS code in the minimal example

        connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .setMaxConnPerRoute(10)
                //.setMaxConnTotal(50)
                //.setTlsSocketStrategy(tlsStrategy)
                .build();

        return connectionManager;
    }

}
