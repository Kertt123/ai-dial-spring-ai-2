package com.serkowski.task14_ums.service;

import com.serkowski.task14_ums.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

public class UserChatClient {

    private final ChatClient chatClient;

    public UserChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatResponse chat(String prompt, String conversationId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(ChatResponse.class);
    }
}
