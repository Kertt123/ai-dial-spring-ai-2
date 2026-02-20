package com.serkowski.task5.controller;

import com.serkowski.task5.clients.DialClient;
import com.serkowski.task5.model.StoreRequest;
import com.serkowski.task5.model.TextRequest;
import com.serkowski.task5.service.VectorStoreService;
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
    Mono<String> store(@RequestBody Mono<StoreRequest> requestBody) {
        return requestBody.flatMap(request -> vectorStoreService.storeAsVector(request.filePath()))
                .then(Mono.just("File stored as vector successfully."));
    }

    @PostMapping("/text")
    Mono<String> text(@RequestBody Mono<TextRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(request.message(), request.conversationId()));
    }
}
