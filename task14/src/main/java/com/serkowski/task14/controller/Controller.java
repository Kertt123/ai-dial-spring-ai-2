package com.serkowski.task14.controller;

import com.serkowski.task14.clients.DialClient;
import com.serkowski.task14.model.ChatResponse;
import com.serkowski.task14.model.TextRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class Controller {

    private final DialClient dialClient;

    public Controller(DialClient dialClient) {
        this.dialClient = dialClient;
    }


    @PostMapping(value = "/text")
    public ResponseEntity<?> chat(@RequestBody TextRequest textRequest) {
        ChatResponse chat = dialClient.chat(textRequest.message(), textRequest.conversationId());
        if (chat.toolResponse() != null && chat.toolResponse().containsKey("encodedImage")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(Base64.getDecoder().decode(chat.toolResponse().get("encodedImage")));
        } else {
            return ResponseEntity.ok(chat.response());
        }
    }

}
