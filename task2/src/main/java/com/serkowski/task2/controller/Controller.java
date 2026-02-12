package com.serkowski.task2.controller;

import com.serkowski.task2.clients.DialClient;
import com.serkowski.task2.model.Task1Request;
import com.serkowski.task2.model.Task2Request;
import com.serkowski.task2.model.Task3Request;
import com.serkowski.task2.model.Task4Request;
import com.serkowski.task2.model.Task5Request;
import com.serkowski.task2.model.Task6Request;
import com.serkowski.task2.model.Task7Request;
import com.serkowski.task2.model.Task8Request;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class Controller {

    private final static String DEFAULT_DEPLOYMENT = "gpt-4o";
    private final static int DEFAULT_N = 1;
    private final static double DEFAULT_TEMP = 1.0;
    private final static int DEFAULT_MAX_TOKENS = 16384;
    private final static double DEFAULT_FREQUENCY_PENALTY = 0.0;
    private final static double DEFAULT_PRESENCE_PENALTY = 0.0;

    @Autowired
    private DialClient dialClient;

    @PostMapping("/task1")
    Mono<String> task1(@RequestBody Mono<Task1Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), request.deploymentName(), DEFAULT_N, DEFAULT_TEMP, ThreadLocalRandom.current().nextLong(), DEFAULT_MAX_TOKENS, DEFAULT_FREQUENCY_PENALTY, DEFAULT_PRESENCE_PENALTY, Collections.emptyList()));
    }

    @PostMapping("/task2")
    Mono<String> task2(@RequestBody Mono<Task2Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, request.n(), DEFAULT_TEMP, ThreadLocalRandom.current().nextLong(), DEFAULT_MAX_TOKENS, DEFAULT_FREQUENCY_PENALTY, DEFAULT_PRESENCE_PENALTY, Collections.emptyList()));
    }

    @PostMapping("/task3")
    Mono<String> task3(@RequestBody Mono<Task3Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, DEFAULT_N, request.temperature(), ThreadLocalRandom.current().nextLong(), DEFAULT_MAX_TOKENS, DEFAULT_FREQUENCY_PENALTY, DEFAULT_PRESENCE_PENALTY, Collections.emptyList()));
    }

    @PostMapping("/task4")
    Mono<String> task4(@RequestBody Mono<Task4Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, DEFAULT_N, DEFAULT_TEMP, request.seed(), DEFAULT_MAX_TOKENS, DEFAULT_FREQUENCY_PENALTY, DEFAULT_PRESENCE_PENALTY, Collections.emptyList()));
    }

    @PostMapping("/task5")
    Mono<String> task5(@RequestBody Mono<Task5Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, DEFAULT_N, DEFAULT_TEMP, ThreadLocalRandom.current().nextLong(), request.maxTokens(), DEFAULT_FREQUENCY_PENALTY, DEFAULT_PRESENCE_PENALTY, Collections.emptyList()));
    }

    @PostMapping("/task6")
    Mono<String> task6(@RequestBody Mono<Task6Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, DEFAULT_N, DEFAULT_TEMP, ThreadLocalRandom.current().nextLong(), DEFAULT_MAX_TOKENS, request.frequencyPenalty(), DEFAULT_PRESENCE_PENALTY, Collections.emptyList()));
    }

    @PostMapping("/task7")
    Mono<String> task7(@RequestBody Mono<Task7Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, DEFAULT_N, DEFAULT_TEMP, ThreadLocalRandom.current().nextLong(), DEFAULT_MAX_TOKENS, DEFAULT_FREQUENCY_PENALTY, request.presencePenalty(), Collections.emptyList()));
    }

    @PostMapping("/task8")
    Mono<String> task8(@RequestBody Mono<Task8Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), DEFAULT_DEPLOYMENT, DEFAULT_N, DEFAULT_TEMP, ThreadLocalRandom.current().nextLong(), DEFAULT_MAX_TOKENS, DEFAULT_FREQUENCY_PENALTY, DEFAULT_PRESENCE_PENALTY, request.stop()));
    }

}
