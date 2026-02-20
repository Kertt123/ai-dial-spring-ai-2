package com.serkowski.task6_1.service;

import com.serkowski.task6_1.model.User;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class UserService {

    private static final String USER_SERVICE_ENDPOINT = "http://localhost:8041";

    private final WebClient webClient;

    public UserService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<User> getAllUsers() {
        return webClient.get()
                .uri(USER_SERVICE_ENDPOINT + "/v1/users")
                .retrieve()
                .bodyToFlux(User.class);
    }
}
