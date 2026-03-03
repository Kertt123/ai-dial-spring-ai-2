package com.serkowski.task12.clients;

import com.serkowski.task12.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatResponse chat(String message, String conversationId) {
       return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(ChatResponse.class);
    }

}