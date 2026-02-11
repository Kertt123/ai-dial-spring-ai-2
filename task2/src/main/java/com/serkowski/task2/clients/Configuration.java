package com.serkowski.task2.clients;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public DialClient dialClient(ChatModel chatModel) {
        return new DialClient(ChatClient.create(chatModel));
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

}
