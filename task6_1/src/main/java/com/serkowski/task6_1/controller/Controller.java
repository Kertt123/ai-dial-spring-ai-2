package com.serkowski.task6_1.controller;

import com.serkowski.task6_1.clients.DialClient;
import com.serkowski.task6_1.model.TextRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

    private final DialClient dialClient;

    public Controller(DialClient dialClient) {
        this.dialClient = dialClient;
    }

    @PostMapping("/userSearch")
    Mono<String> userSearch(@RequestBody Mono<TextRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.findUserByUserQuery(request.message(), request.conversationId()));
    }
}
