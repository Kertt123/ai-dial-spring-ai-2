package com.serkowski.task2.controller;

import com.serkowski.task2.clients.DialClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private DialClient dialClient;

    @PostMapping("/task1")
    Mono<String> task1(@RequestBody Mono<Task1Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), request.deploymentName(), 1));
    }

    @PostMapping("/task2")
    Mono<String> task1(@RequestBody Mono<Task1Request> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletions(List.of(UserMessage.builder()
                .text(request.message())
                .build()), request.deploymentName(), 1));
    }

}
