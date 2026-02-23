package com.serkowski.task6_2.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.task6_2.model.SearchRequest;
import com.serkowski.task6_2.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Service
public class DialClient {

    private static final String QUERY_ANALYSIS_PROMPT = """
            You are a query analysis system that extracts search parameters from user questions about users.
            
            ## Available Search Fields:
            - **name**: User's first name (e.g., "John", "Mary")
            - **surname**: User's last name (e.g., "Smith", "Johnson") 
            - **email**: User's email address (e.g., "john@example.com")
            
            ## Instructions:
            1. Analyze the user's question and identify what they're looking for
            2. Extract specific search values mentioned in the query
            3. Map them to the appropriate search fields
            4. If multiple search criteria are mentioned, include all of them
            5. Only extract explicit values - don't infer or assume values not mentioned
            
            ## Examples:
            - "Who is John?" → name: "John"
            - "Find users with surname Smith" → surname: "Smith" 
            - "Look for john@example.com" → email: "john@example.com"
            - "Find John Smith" → name: "John", surname: "Smith"
            - "I need user emails that filled with hiking" → No clear search parameters (return empty list)
            
            """;
    private static final String USER_PROMPT = """
            ## RAG CONTEXT:
            {context}
            
            ## USER QUESTION: 
            {query}""";

    private static final String SYSTEM_PROMPT = """
            You are a RAG-powered assistant that assists users with their questions about user information.
            
            ## Structure of User message:
            `RAG CONTEXT` - Retrieved documents relevant to the query.
            `USER QUESTION` - The user's actual question.
            
            ## Instructions:
            - Use information from `RAG CONTEXT` as context when answering the `USER QUESTION`.
            - Cite specific sources when using information from the context.
            - Answer ONLY based on conversation history and RAG context.
            - If no relevant information exists in `RAG CONTEXT` or conversation history, state that you cannot answer the question.
            - Be conversational and helpful in your responses.
            - When presenting user information, format it clearly and include relevant details.
            """;

    ChatClient chatClient;
    UserService userService;
    ObjectMapper objectMapper = new ObjectMapper();

    public DialClient(ChatClient chatClient, UserService userService) {
        this.chatClient = chatClient;
        this.userService = userService;
    }

    public Mono<String> findUserByUserQuery(String message, String conversationId) {
        return Mono.fromCallable(() -> chatClient.prompt()
                        .system(QUERY_ANALYSIS_PROMPT)
                        .user(message)
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .call()
                        .entity(SearchRequest.class))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(searchRequest -> userService.searchUsers(searchRequest)
                        .collectList()
                        .flatMap(result -> chatClient.prompt()
                                .system(SYSTEM_PROMPT)
                                .user(userSpec -> userSpec.text(USER_PROMPT)
                                        .param("context", toJson(result))
                                        .param("query", message))
                                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                                .stream()
                                .content()
                                .collect(Collectors.joining())));
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
