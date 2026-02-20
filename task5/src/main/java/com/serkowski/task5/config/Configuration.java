package com.serkowski.task5.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.serkowski.task5.clients.DialClient;
import com.serkowski.task5.service.VectorStoreService;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public DialClient dialClient(ChatModel chatModel, VectorStore vectorStore, OpenAIClientBuilder openAIClientBuilder) {
        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
                                .build(),
                        new SimpleLoggerAdvisor(),
                        RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(VectorStoreDocumentRetriever.builder()
                                        .similarityThreshold(0.3)
                                        .topK(4)
                                        .vectorStore(vectorStore)
                                        .build())
                                .queryTransformers(RewriteQueryTransformer.builder()
                                        .chatClientBuilder(ChatClient.builder(AzureOpenAiChatModel.builder()
                                                .openAIClientBuilder(openAIClientBuilder)
                                                .defaultOptions(AzureOpenAiChatOptions.builder()
                                                        .deploymentName("gpt-4.1-nano-2025-04-14")
                                                        .build())
                                                .build()))
                                        .build())
                                .build()

                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .build());
    }

    @Bean
    public VectorStoreService vectorStoreService(VectorStore vectorStore, KeywordMetadataEnricher keywordMetadataEnricher) {
        return new VectorStoreService(vectorStore, keywordMetadataEnricher);
    }

    @Bean
    public KeywordMetadataEnricher keywordMetadataEnricher(OpenAIClientBuilder openAIClientBuilder) {
        return KeywordMetadataEnricher.builder(AzureOpenAiChatModel.builder()
                        .openAIClientBuilder(openAIClientBuilder)
                        .defaultOptions(AzureOpenAiChatOptions.builder()
                                .deploymentName("gpt-4.1-nano-2025-04-14")
                                .build())
                        .build())
                .keywordCount(5)
                .build();
    }

}
