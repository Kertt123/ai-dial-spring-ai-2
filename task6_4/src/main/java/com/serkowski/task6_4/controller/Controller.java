package com.serkowski.task6_4.controller;

import com.serkowski.task6_4.clients.DialClient;
import com.serkowski.task6_4.model.TextRequest;
import com.serkowski.task6_4.model.User;
import com.serkowski.task6_4.service.VectorStoreService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

    private final DialClient dialClient;
    private final VectorStoreService vectorStoreService;

    public Controller(DialClient dialClient, VectorStoreService vectorStoreService) {
        this.dialClient = dialClient;
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/store")
    Mono<String> store() {
        return vectorStoreService.storeUsersAsVector()
                .then(Mono.just("File stored as vector successfully."));
    }

    @PostMapping("/userSearch")
    Flux<User> userSearch(@RequestBody Mono<TextRequest> requestBody) {
        return requestBody.flatMapMany(request -> dialClient.findUserByUserQuery(request.message(), request.conversationId()));
    }
}
