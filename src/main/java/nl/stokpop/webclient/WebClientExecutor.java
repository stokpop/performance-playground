package nl.stokpop.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WebClientExecutor {

//    public static final long SLEEP_DURATION_MILLIS = Duration.ofMillis(1).toMillis();
    public static final long SLEEP_DURATION_MILLIS = Duration.ofSeconds(40).toMillis();

    public static void main(String[] args) {

        // Setup WireMock server
        WireMockServer wireMockServer = WebClientWiremockServer.createWiremockServer();
        wireMockServer.start();

        String baseUrl = "https://localhost:" + WebClientWiremockServer.HTTPS_PORT;

        WebClient webClient = WebClientConfig.createWebClient(baseUrl);

        WebClient.Builder webClientBuilder = WebClientConfig.createWebClientBuilder(baseUrl);

        WebClientCaller webClientCaller = new WebClientCaller(webClient, webClientBuilder);

        List<CompletableFuture<String>> futures = new ArrayList<>();
        int numberOfCalls = 2;
        for (int i = 0; i < numberOfCalls; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                String authToken = java.util.UUID.randomUUID().toString();
                String result = webClientCaller.callWebClient(authToken);
                if (!result.contains(authToken)) {
                    //throw new IllegalStateException("Response does not contain auth token: " + authToken + " in " + resultWebClient);
                    System.out.println("WARNING: Response does not contain auth token: " + authToken + " in " + result);
                }
                System.out.println("Result from WebClient: " + result);
                return result;
            }));

//            futures.add(CompletableFuture.supplyAsync(() -> {
//                String authToken = java.util.UUID.randomUUID().toString();
//                String result = webClientCaller.callWebClientBuilder(authToken);
//                if (!result.contains(authToken)) {
//                    //throw new IllegalStateException("Response does not contain auth token: " + authToken + " in " + result);
//                    System.out.println("WARNING: Response does not contain auth token: " + authToken + " in " + result);
//                }
//                System.out.println("Result from WebClientBuilder: " + result);
//                return result;
//            }));

            try {
                Thread.sleep(SLEEP_DURATION_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        System.out.println("All requests completed.");

        wireMockServer.stop();
    }


}
