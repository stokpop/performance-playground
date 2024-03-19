package nl.stokpop.httpclient;

import nl.stokpop.kotlin.httpclient.HttpClientDefaultConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

public class HttpClientExtravaganza {

    private static final CredentialsProvider CREDENTIALS = new CredentialsProvider() {
        @Override
        public void setCredentials(AuthScope authscope, Credentials credentials) {

        }

        @Override
        public Credentials getCredentials(AuthScope authscope) {
            return null;
        }

        @Override
        public void clear() {

        }
    };

    public void fake() {
        boolean check = new HttpClientDefaultConfig().createHttpClient() == null;
    }

    public CloseableHttpClient createHttpClientOne() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(350)
            .setConnectTimeout(250)
            .setSocketTimeout(4000)
            .build(); // good, all timeouts set

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig) // good, all timeouts set, if missing > default timeouts?
            .build();
    }

    public CloseableHttpClient createHttpClientTwo() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(4000)
            .build(); // bad, not all timeouts set

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig) // good, all timeouts set, if missing > default timeouts?
            .build();
    }

    public CloseableHttpClient createHttpClientThree() {
        return HttpClientBuilder.create() // bad, no setDefaultRequestConfig called with explicit timeouts
            .setConnectionTimeToLive(180, TimeUnit.SECONDS)
            .build();
    }

    public CloseableHttpClient createHttpClientThreePointFive(HttpClientBuilder builder) {
        return builder // bad, no setDefaultRequestConfig called with explicit timeouts
            .setConnectionTimeToLive(180, TimeUnit.SECONDS)
            .build();
    }

    public CloseableHttpAsyncClient createHttpClientFour() {
        return HttpAsyncClientBuilder.create() // bad, no setDefaultRequestConfig called with explicit timeouts
            .disableAuthCaching()
            .build();
    }

    public CloseableHttpClient createHttpClientFive() {
        return HttpClients.custom() // bad, no setDefaultRequestConfig called with explicit timeouts
            .build();
    }

    public HttpClient createHttpClientSix() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(); // good, with all three timeout calls
        factory.setConnectTimeout(250);
        factory.setConnectionRequestTimeout(350);
        factory.setReadTimeout(4000);

        return factory.getHttpClient();
    }

    public HttpClient createHttpClientSeven() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(); // bad, not all three timeout calls
        factory.setReadTimeout(4000);

        return factory.getHttpClient();
    }

    public HttpClient createHttpClientEight(HttpComponentsClientHttpRequestFactory factory) {
        factory.setReadTimeout(4000); // bad, not all three timeout calls
        return factory.getHttpClient();
    }

    public RestTemplate test(final Integer maxConnections, final Integer maxConnectionsPerHost,
                             final Integer connectionTimeoutInMilliSeconds, final Integer readTimeoutInMilliSeconds, final SSLContext sslContext) {

            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = sslContext != null ?
                getPoolingHttpClientConnectionManagerWithSslEnabled(sslContext, maxConnections, maxConnectionsPerHost) :
                getPoolingHttpClientConnectionManagerWithoutSslEnabled(maxConnections, maxConnectionsPerHost);

            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeoutInMilliSeconds)
                .setConnectionRequestTimeout(connectionTimeoutInMilliSeconds)
                .setSocketTimeout(readTimeoutInMilliSeconds)
                .build();

            HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .disableRedirectHandling()
                .disableConnectionState()
                .build();

            try {
                HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new  HttpComponentsClientHttpRequestFactory(httpClient);  // false positive
                //httpComponentsClientHttpRequestFactory.setHttpClient(httpClient);
                return new RestTemplate(httpComponentsClientHttpRequestFactory);
            } catch (Exception exception) {
                throw new RuntimeException("Error while creating Http Connection Factory");
            }
        }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManagerWithoutSslEnabled(Integer maxConnections, Integer maxConnectionsPerHost) {
        return null;
    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManagerWithSslEnabled(SSLContext sslContext, Integer maxConnections, Integer maxConnectionsPerHost) {
        return null;
    }

    public HttpComponentsClientHttpRequestFactory bad3() {

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(350)
            .setConnectTimeout(250)
            .setSocketTimeout(100)
            .build(); // bad, socketTimeout is missing, trigger is below on setHttpClient

        HttpClient client = HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .build();

        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(client); // bad

        return factory;
    }

}
