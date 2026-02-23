package com.serkowski.task6_2.service;

import com.serkowski.task6_2.model.SearchRequest;
import com.serkowski.task6_2.model.User;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Flux<User> searchUsers(SearchRequest searchRequest) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.scheme("http");
                    uriBuilder.host("localhost");
                    uriBuilder.port(8041);
                    uriBuilder.path( "/v1/users/search");
                    uriBuilder.queryParamIfPresent("name", Optional.ofNullable(searchRequest.name()));
                    uriBuilder.queryParamIfPresent("surname", Optional.ofNullable(searchRequest.surname()));
                    uriBuilder.queryParamIfPresent("email", Optional.ofNullable(searchRequest.email()));
                    uriBuilder.queryParamIfPresent("gender", Optional.ofNullable(searchRequest.gender()));
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToFlux(User.class);
    }
}
