package com.serkowski.task4.config;

import com.serkowski.task4.clients.DialClient;
import com.serkowski.task4.service.VectorStoreService;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {


    @Bean
    public DialClient dialClient(ChatModel chatModel, VectorStore vectorStore) {
        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                        You are a RAG-powered assistant that assists users with their questions about microwave usage.
                        
                        ## Structure of User message:
                        `RAG CONTEXT` - Retrieved documents relevant to the query.
                        `USER QUESTION` - The user's actual question.
                        
                        ## Instructions:
                        - Use information from `RAG CONTEXT` as context when answering the `USER QUESTION`.
                        - Cite specific sources when using information from the context.
                        - Answer ONLY based on conversation history and RAG context.
                        - If no relevant information exists in `RAG CONTEXT` or conversation history, state that you cannot answer the question.
                        
                        ##RAG CONTEXT:
                        <question_answer_context>
                        
                        
                        ##USER QUESTION:
                        <query>""")
                .build();

        return new DialClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
                                .build(),
                        new SimpleLoggerAdvisor(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .promptTemplate(customPromptTemplate)
                                .searchRequest(SearchRequest.builder().similarityThreshold(0.3d).topK(4).build())
                                .build()

                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .build());
    }

    @Bean
    public VectorStoreService vectorStoreService(VectorStore vectorStore) {
        return new VectorStoreService(vectorStore);
    }

}
