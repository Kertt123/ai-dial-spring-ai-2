package com.serkowski.task12.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.serkowski.task12.clients.DialClient;
import com.serkowski.task12.service.DialBucketClient;
import com.serkowski.task12.service.ImageGenerationService;
import com.serkowski.task12.service.VectorStoreService;
import com.serkowski.task12.tools.FileExtractor;
import com.serkowski.task12.tools.ImageGenerationTool;
import com.serkowski.task12.tools.RagTool;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@org.springframework.context.annotation.Configuration
public class Configuration {


    @Bean
    public DialClient dialClient(ChatModel chatModel, ImageGenerationTool imageGenerationTool, SyncMcpToolCallbackProvider toolCallbackProvider, RagTool ragTool) {
        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
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
                .defaultTools(imageGenerationTool, new FileExtractor(), ragTool)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build());
    }

    @Bean
    @Qualifier("ragClient")
    public ChatClient ragClient(ChatModel chatModel, VectorStore vectorStore, OpenAIClientBuilder openAIClientBuilder) {
        return ChatClient.builder(chatModel)
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
                .build();
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

    @Bean
    public DialBucketClient dialBucketClient(RestTemplate restTemplate, @Value("${spring.ai.azure.openai.endpoint}") String url, @Value("${spring.ai.azure.openai.api-key}") String apiKey) {
        return new DialBucketClient(restTemplate, url, apiKey);
    }

    @Bean
    public ImageGenerationService imageGenerationService(RestTemplate restTemplate, @Value("${spring.ai.azure.openai.endpoint}") String url, @Value("${spring.ai.azure.openai.api-key}") String apiKey, DialBucketClient dialBucketClient) {
        return new ImageGenerationService(url, apiKey, dialBucketClient, restTemplate);
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

    @Bean
    public RagTool ragTool(ChatClient ragClient, VectorStoreService vectorStoreService) {
        return new RagTool(ragClient, vectorStoreService);
    }

}
