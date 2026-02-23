package com.serkowski.task6_4.service;

import com.serkowski.task6_4.model.User;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
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
                                .map(user -> newUserDocument(user.id(), user.about_me()))
                                .collect(Collectors.toList())))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }


    public Mono<Void> syncVectorStore() {
        return userService.getAllUsers()
                .collectMap(User::id, User::about_me)
                .flatMap(userMap -> Mono.fromRunnable(() -> {
                    var exp = new FilterExpressionBuilder();
                    var documents = vectorStore.similaritySearch(SearchRequest.builder()
                                    .filterExpression(exp.isNotNull("userId").build())
                                    .similarityThreshold(0.1)
                                    .topK(2000)
                                    .build())
                            .stream()
                            .collect(Collectors.toMap(document -> Integer.valueOf(document.getMetadata().get("userId").toString()), document -> document));
                    List<Document> newDocuments = new ArrayList<>();
                    documents.forEach((userId, document) -> {
                        if (!userMap.containsKey(userId)) {
                            vectorStore.delete(document.getId());
                        }
                    });
                    userMap.forEach((userId, aboutMe) -> {
                        if (!documents.containsKey(userId)) {
                            newDocuments.add(newUserDocument(userId, aboutMe));
                        }
                    });
                    if (!newDocuments.isEmpty()) {
                        vectorStore.accept(newDocuments);
                    }
                }).subscribeOn(Schedulers.boundedElastic())) // Jeden scheduler dla całości
                .then();
    }

    private static @NonNull Document newUserDocument(Integer userId, String aboutMe) {
        return new Document(String.format("User ID: %s, About Me: %s", userId, aboutMe), Map.of("userId", userId,
                "aboutMe", aboutMe));
    }
}
