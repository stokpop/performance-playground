package nl.stokpop.webclient;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientCaller {

    private WebClient webClient;
    private WebClient.Builder webClientBuilder;

    public WebClientCaller(WebClient webClient, WebClient.Builder webClientBuilder) {
        this.webClient = webClient;
        this.webClientBuilder = webClientBuilder;
    }

    public String callWebClient(String authToken) {
        return webClient.get()
                .header("X-Auth-Token", authToken)
                .retrieve().bodyToMono(String.class).block() ;
    }

    public String callWebClientBuilder(String authToken) {
        return webClientBuilder
                .defaultHeaders(headers -> {
                    headers.set("X-Auth-Token", authToken);
                    headers.set("Content-Type", "application/json");
                    headers.set("Accept", "application/json");
                })
                .build()
                .get()
                //.header("X-Auth-Token", authToken) // This is safe way to set X-Auth-Token header
                .retrieve().bodyToMono(String.class).block() ;
    }
}
