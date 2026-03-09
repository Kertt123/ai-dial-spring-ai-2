package com.serkowski.task14.clients;

import com.serkowski.task14.model.ChatResponse;
import com.serkowski.task14.service.MemoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

public class DialClient {

    private final ChatClient chatClient;
    private final MemoryService memoryService;

    public DialClient(ChatClient chatClient, MemoryService memoryService) {
        this.chatClient = chatClient;
        this.memoryService = memoryService;
    }

    public ChatResponse chat(String message, String conversationId) {
       return chatClient.prompt()
                .user(message)
               .system(systemSpec -> systemSpec.param("USER_INFO", memoryService.getUserMemory("userId")))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(ChatResponse.class);
    }

}