package com.serkowski.clients;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public DialClient dialClient(ChatModel chatModel, StreamingChatModel streamingChatModel) {
        return new DialClient(chatModel, streamingChatModel);
    }
}
