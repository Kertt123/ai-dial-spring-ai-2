package com.serkowski.task12.controller;

import com.serkowski.task12.clients.DialClient;
import com.serkowski.task12.model.ChatResponse;
import com.serkowski.task12.model.StoreRequest;
import com.serkowski.task12.model.TextRequest;
import com.serkowski.task12.service.VectorStoreService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class Controller {

    private final DialClient dialClient;
    private final VectorStoreService vectorStoreService;

    public Controller(DialClient dialClient, VectorStoreService vectorStoreService) {
        this.dialClient = dialClient;
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/store")
    String store(@RequestBody StoreRequest request) {
        vectorStoreService.storeAsVector(request.filePath());
        return "File stored as vector successfully.";
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
