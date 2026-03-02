package com.serkowski.task11.controller;

import com.serkowski.task11.clients.DialClient;
import com.serkowski.task11.model.ChatResponse;
import com.serkowski.task11.model.Conversation;
import com.serkowski.task11.model.ConversationSummary;
import com.serkowski.task11.model.CreateConversationRequest;
import com.serkowski.task11.model.TextRequest;
import com.serkowski.task11.service.ConversationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class Controller {

    private final DialClient dialClient;
    private final ConversationService conversationService;

    public Controller(DialClient dialClient, ConversationService conversationService) {
        this.dialClient = dialClient;
        this.conversationService = conversationService;
    }

    @PostMapping("/conversations")
    public ConversationSummary newConversation(@RequestBody CreateConversationRequest createConversationRequest) {
        return conversationService.saveConversationSummary(createConversationRequest.title());
    }

    @GetMapping("/conversations")
    public List<ConversationSummary> conversations() {
        return conversationService.getConversations();
    }

    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversationById(@PathVariable("conversationId") String conversationId) {
        return conversationService.getConversationById(conversationId);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public void deleteConversation(@PathVariable("conversationId") String conversationId) {
        conversationService.deleteConversationById(conversationId);
    }

    @PostMapping(value = "/conversations/{conversationId}/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chat(@PathVariable("conversationId") String conversationId, @RequestBody TextRequest textRequest) {
        return dialClient.chat(textRequest.message().content(), conversationId);
    }

}
