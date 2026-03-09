package com.serkowski.task14_ums.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

public class UserChatClient {

    private final ChatClient chatClient;

    public UserChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String prompt, String conversationId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
