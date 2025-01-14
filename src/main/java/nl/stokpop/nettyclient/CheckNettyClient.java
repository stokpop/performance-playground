package nl.stokpop.nettyclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import nl.stokpop.WatskeburtException;
import org.jetbrains.annotations.NotNull;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckNettyClient {

    public static void main(String[] args) {
        CheckNettyClient checkNettyClient = new CheckNettyClient();
        checkNettyClient.createNettyClient();
    }

    private void createNettyClient() {
        ConnectionProvider provider =
                ConnectionProvider.builder("custom")
                        .maxConnections(1) // when omitted defaults to 2 x number of available processors, e.g. 20 on mac m1 laptop
                        .pendingAcquireMaxCount(1) // reduce for fail fast behavior
                        .pendingAcquireTimeout(Duration.ofSeconds(4)) // reduce for fail fast behavior
                        .lifo() // recommended over fifo for most cases
                        .maxIdleTime(Duration.ofSeconds(20)) // not sure what is recommended here
                        .maxLifeTime(Duration.ofSeconds(60)) // not sure what is recommended here
                        .evictInBackground(Duration.ofSeconds(120)) // not sure what is recommended here
                        .build();


        HttpClient client = HttpClient.create(provider)
                .secure(CheckNettyClient::configureSslProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(5))
                .wiretap(true); // log requests and responses

        client.warmup().block();

        int parallelCalls = 4;
        ExecutorService executor = Executors.newFixedThreadPool(parallelCalls);
        try {
            for (int i = 0; i < parallelCalls; i++) {
                executor.submit(() -> {
                    final long startTimeMillis = System.currentTimeMillis();
                    client.get()
                            .uri("https://httpbin.org/delay/1")
                            //.uri("https://10.255.255.1/delay/6") // force connection timeout
                            .responseContent()
                            .aggregate()
                            .asString()
                            .doOnNext(CheckNettyClient::reportResponse)
                            .doOnError(CheckNettyClient::reportError)
                            .block();
                    System.out.println(Thread.currentThread().getName() + " total time to response: " + (System.currentTimeMillis() - startTimeMillis) + " ms");
                });
            }
        } finally {
            executor.shutdown();
        }
    }

    private static void reportResponse(String response) {
        System.out.println("Response: " + response);
    }

    @NotNull
    private static SslProvider configureSslProvider(SslProvider.SslContextSpec spec) {
        try {
            return spec.sslContext(SslContextBuilder.forClient().build())
                    .handshakeTimeout(Duration.ofMillis(800)) // default 10 seconds
                    .closeNotifyFlushTimeout(Duration.ofSeconds(2)) // default 3 seconds
                    .closeNotifyReadTimeout(Duration.ofSeconds(2)) // default 0 (disabled)
                    .build();
        } catch (javax.net.ssl.SSLException e) {
            throw new WatskeburtException("Error building SSL context", e);
        }
    }

    private static void reportError(Throwable throwable) {
        System.err.println("Error handler: " + throwable);
        throwable.printStackTrace();
    }
}
