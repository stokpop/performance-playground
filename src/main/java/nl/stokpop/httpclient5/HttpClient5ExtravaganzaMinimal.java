package nl.stokpop.httpclient5;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HttpClient5ExtravaganzaMinimal {

    @Bean
    public CloseableHttpClient httpClient5minimalOtherWithMethodCall(SslBundles sslBundles) {

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setTimeToLive(TimeValue.ofSeconds(60)) // TTL for persistent connections
                //.setConnectTimeout(Timeout.ofMilliseconds(300))
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
                .setMaxConnTotal(50)
                //.setTlsSocketStrategy(tlsStrategy)
                .build();

        return connectionManager;
    }

}
