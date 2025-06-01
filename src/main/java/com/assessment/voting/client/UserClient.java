package com.assessment.voting.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {
    private static final Logger log = LoggerFactory.getLogger(UserClient.class);
    private String url;
    private WebClient webClient;


    public UserClient(@Value("${api.user.url}") String url, WebClient.Builder webClientBuilder) {
        this.url = url;
        this.webClient = webClientBuilder.baseUrl(url).build();
    }


    @CircuitBreaker(name = "user-api", fallbackMethod = "fallback")
    public Mono<String> validateCpf(String cpf){
        return webClient
                .get()
                .uri(url + "users/" + cpf)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fallback(Throwable throwable) {
        log.warn("Fallback triggered for validateCpf due to: {}", throwable.getMessage());
        if(RandomUtils.nextBoolean()) {
            return Mono.just("ABLE_TO_VOTE");
        } else {
            return Mono.just("UNABLE_TO_VOTE");
        }
    }
}
