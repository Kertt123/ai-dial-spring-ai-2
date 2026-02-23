package com.serkowski.task6_3.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.stream.Collectors;

public class VectorStoreService {

    private final VectorStore vectorStore;
    private final UserService userService;

    public VectorStoreService(VectorStore vectorStore, UserService userService) {
        this.vectorStore = vectorStore;
        this.userService = userService;
    }

    public Mono<Void> storeUsersAsVector() {
        return userService.getAllUsers()
                .buffer(100)
                .flatMap(userBatch -> Mono.fromRunnable(() -> vectorStore.accept(userBatch.stream()
                                .map(user -> new Document(user.toString(), Map.of("name", user.name(),
                                        "surname", user.surname(),
                                        "email", user.email(),
                                        "gender", user.gender())))
                                .collect(Collectors.toList())))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }
}
