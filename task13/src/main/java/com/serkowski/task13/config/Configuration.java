package com.serkowski.task13.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.serkowski.task13.advisor.LongMemoryAdvisor;
import com.serkowski.task13.clients.DialClient;
import com.serkowski.task13.repository.MemoryRepository;
import com.serkowski.task13.service.DialBucketClient;
import com.serkowski.task13.service.ImageGenerationService;
import com.serkowski.task13.service.MemoryService;
import com.serkowski.task13.service.VectorStoreService;
import com.serkowski.task13.tools.FileExtractor;
import com.serkowski.task13.tools.ImageGenerationTool;
import com.serkowski.task13.tools.RagTool;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
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

import static com.serkowski.task13.config.Prompts.SYSTEM_PROMPT;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public MemoryService memoryService(MemoryRepository memoryRepository) {
        return new MemoryService(memoryRepository);
    }

    @Bean
    public LongMemoryAdvisor getLongMemoryAdvisor(ChatModel chatModel, ChatMemory chatMemory, MemoryService memoryService) {
        return new LongMemoryAdvisor(ChatClient.builder(chatModel)
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4.1-nano-2025-04-14")
                        .build())
                .build(), chatMemory, memoryService);
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(30)
                .build();
    }


    @Bean
    public DialClient dialClient(ChatModel chatModel,
                                 ChatMemory chatMemory,
                                 ImageGenerationTool imageGenerationTool,
                                 SyncMcpToolCallbackProvider toolCallbackProvider,
                                 RagTool ragTool,
                                 LongMemoryAdvisor longMemoryAdvisor,
                                 MemoryService memoryService) {
        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build(),
                        new SimpleLoggerAdvisor(),
                        StructuredOutputValidationAdvisor.builder()
                                .maxRepeatAttempts(3)
                                .outputType(ChatResponse.class)
                                .build(),
                        longMemoryAdvisor
                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .defaultTools(imageGenerationTool, new FileExtractor(), ragTool)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .defaultSystem(SYSTEM_PROMPT)
                .build(), memoryService);
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
                                        .promptTemplate(new PromptTemplate("Given a user query, rewrite it to provide better results when querying a {target}.\nRemove any irrelevant information, and ensure the query is concise and specific and translate to english if is in other language.\n\nOriginal query:\n{query}\n\nRewritten query:\n"))
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
