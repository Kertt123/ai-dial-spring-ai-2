package com.serkowski.task6_3.clients;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Mono<String> findUserByUserQuery(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .collect(Collectors.joining());
    }

}
