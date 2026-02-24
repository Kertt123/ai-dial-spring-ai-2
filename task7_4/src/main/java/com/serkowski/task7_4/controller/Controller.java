package com.serkowski.task7_4.controller;

import com.serkowski.task7_4.clients.DialClient;
import com.serkowski.task7_4.model.TextRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

    private final DialClient dialClient;

    public Controller(DialClient dialClient) {
        this.dialClient = dialClient;
    }

    @PostMapping("/text")
    Mono<String> text(@RequestBody Mono<TextRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.chat(request.message(), request.conversationId()));
    }

    /**
     * Buffers ALL chunks, then redacts PII from the full response ("Shrek" approach).
     */
    @PostMapping("/text/redacted")
    Mono<String> textRedacted(@RequestBody Mono<TextRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.chatWithRedaction(request.message(), request.conversationId()));
    }

    /**
     * Streaming variant — buffers chunks in windows of 5, redacts each window,
     * and streams them back progressively.
     */
    @PostMapping(value = "/text/redacted/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> textRedactedStream(@RequestBody TextRequest request) {
        return dialClient.chatWithStreamingRedaction(request.message(), request.conversationId(), 5);
    }
}
