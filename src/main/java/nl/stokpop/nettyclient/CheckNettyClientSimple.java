package nl.stokpop.nettyclient;

import reactor.netty.http.client.HttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckNettyClientSimple {

    public static void main(String[] args) {

        CheckNettyClientSimple checkNettyClient = new CheckNettyClientSimple();
        HttpClient client = HttpClient.create()
                .responseTimeout(java.time.Duration.ofSeconds(3))
                .doOnChannelInit((connectionObserver, channel, remoteAddress) -> {
                            channel.pipeline().addFirst("yourHandler", new DelayedMessageHandler(500));
                            //channel.pipeline().addBefore("reactor.left.httpCodec", "yourHandler", new YourHandler());
                            channel.pipeline().forEach(a -> System.out.println("PIPELINE: " + a));
                        }
                );

        //checkNettyClient.doParallelCalls(client, 2);
        checkNettyClient.doSomeCalls(client, 3);
    }

    private void doParallelCalls(HttpClient client, int parallelCalls) {

        ExecutorService executor = Executors.newFixedThreadPool(parallelCalls);
        try {
            for (int i = 0; i < parallelCalls; i++) {
                executor.submit(() -> {
                    doCall(client);
                });
            }
        } finally {
            executor.shutdown();
        }
    }

    private void doSomeCalls(HttpClient client, int calls) {
        client.warmup().block();
        for (int i = 0; i < calls; i++) {
            doCall(client);
        }
    }

    private static void doCall(HttpClient client) {
        final long startTimeMillis = System.currentTimeMillis();

        String answer = client.get()
                .uri("http://localhost:8080/delay")
                .responseContent()
                .aggregate()
                .asString()
                .doOnError(throwable -> reportError(throwable, System.currentTimeMillis() - startTimeMillis))
                .block();

        long durationMillis = System.currentTimeMillis() - startTimeMillis;
        log("Answer: " + answer + " --- Thread.currentThread().getName() total time to response: " + durationMillis + " ms");
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

    private static void reportResponse(String response) {
        log("Response: " + response);
    }

    private static void reportError(Throwable throwable, long durationMillis) {
        log("Error handler (after " + durationMillis + " ms): " + throwable);
        throwable.printStackTrace();
    }
}
