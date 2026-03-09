package com.serkowski.task14.config;

import com.serkowski.task14.clients.DialClient;
import com.serkowski.task14.model.ChatResponse;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.serkowski.task14.config.Prompts.SYSTEM_PROMPT;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(30)
                .build();
    }


    @Bean
    public DialClient dialClient(ChatModel chatModel,
                                 ChatMemory chatMemory,
                                 SyncMcpToolCallbackProvider toolCallbackProvider) {
        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build(),
                        new SimpleLoggerAdvisor(),
                        StructuredOutputValidationAdvisor.builder()
                                .maxRepeatAttempts(3)
                                .outputType(ChatResponse.class)
                                .build()
                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .defaultToolCallbacks(Arrays.stream(toolCallbackProvider.getToolCallbacks())
                        .filter(tool -> tool.getToolDefinition().name().contains("agent"))
                        .toList())
                .defaultSystem(SYSTEM_PROMPT)
                .build());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "OPTIONS", "DELETE", "PUT")
                        .allowedHeaders("*");
            }
        };
    }


}
