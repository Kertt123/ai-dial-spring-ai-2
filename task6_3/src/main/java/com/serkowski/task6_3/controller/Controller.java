package com.serkowski.task6_3.controller;

import com.serkowski.task6_3.clients.DialClient;
import com.serkowski.task6_3.model.TextRequest;
import com.serkowski.task6_3.service.VectorStoreService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    Mono<String> userSearch(@RequestBody Mono<TextRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.findUserByUserQuery(request.message(), request.conversationId()));
    }
}
