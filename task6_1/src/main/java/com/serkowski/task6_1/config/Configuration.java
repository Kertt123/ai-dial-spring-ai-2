package com.serkowski.task6_1.config;

import com.serkowski.task6_1.clients.DialClient;
import com.serkowski.task6_1.service.UserService;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public DialClient dialClient(ChatModel chatModel, UserService userService){
        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
                                .build(),
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .build(), userService);
    }

    @Bean
    public UserService userService(WebClient webClient) {
        return new UserService(webClient);
    }

    @Bean
    public WebClient webClient() {
        final int size = 16 * 1024 * 1024; // 16 MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }
}
