package com.serkowski.task6_3.config;

import com.serkowski.task6_3.clients.DialClient;
import com.serkowski.task6_3.service.UserService;
import com.serkowski.task6_3.service.VectorStoreService;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public DialClient dialClient(ChatModel chatModel, VectorStore vectorStore) {
        String systemPrompt = """
                You are a helpful assistant that answers questions about users based on the provided context.
                
                INSTRUCTIONS:
                1. Analyze the provided context containing user information
                2. Answer the user's question based ONLY on the information in the context
                3. If the context doesn't contain enough information to answer the question, say so
                4. Be concise and accurate in your responses
                5. If multiple users match the query, list all of them""";

        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
                                .build(),
                        new SimpleLoggerAdvisor(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.1)
                                        .topK(10)
                                        .build())
                                .build()
                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .defaultSystem(systemPrompt)
                .build());
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

    @Bean
    public VectorStoreService vectorStoreService(VectorStore vectorStore, UserService userService) {
        return new VectorStoreService(vectorStore, userService);
    }
}
