package com.serkowski.task9.controller;

import com.serkowski.task9.clients.DialClient;
import com.serkowski.task9.model.TextRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final DialClient dialClient;

    public Controller(DialClient dialClient) {
        this.dialClient = dialClient;
    }

    @PostMapping("/text")
    String text(@RequestBody TextRequest request) {
        return dialClient.chat(request.message(), request.conversationId());
    }
}
