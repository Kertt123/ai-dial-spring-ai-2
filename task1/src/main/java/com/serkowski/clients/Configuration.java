package com.serkowski.clients;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public DialCustomClient dialCustomClient(WebClient webClient, @Value("${spring.ai.azure.openai.endpoint}") String url, @Value("${spring.ai.azure.openai.api-key}") String apiKey) {
        return new DialCustomClient(webClient, url, apiKey);
    }
}
