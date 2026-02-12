package com.serkowski.task3.clients;

import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {


    @Bean
    public DialClient dialClient(ChatModel chatModel) {
        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
                                .build()
                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .build());
    }

}
