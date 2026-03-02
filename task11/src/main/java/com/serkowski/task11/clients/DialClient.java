package com.serkowski.task11.clients;

import com.serkowski.task11.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Flux<ChatResponse> chat(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .chatResponse()
                .map(this::toChatResponse);
    }


    private ChatResponse toChatResponse(org.springframework.ai.chat.model.ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse)
                .map(org.springframework.ai.chat.model.ChatResponse::getResult)
                .map(generation -> {
                    String text = generation.getOutput().getText();

                    boolean isFinished = Optional.ofNullable(generation.getOutput().getMetadata().get("finishReason"))
                            .map("stop"::equals)
                            .orElse(false);

                    return new ChatResponse(text, isFinished);
                }).orElseThrow(() -> new RuntimeException("No chat response"));

    }
}