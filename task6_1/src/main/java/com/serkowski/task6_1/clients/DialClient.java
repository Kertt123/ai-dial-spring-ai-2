package com.serkowski.task6_1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.task6_1.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DialClient {
    private static final String BATCH_SYSTEM_PROMPT = """
            You are a user search assistant. Your task is to find users from the provided list that match the search criteria.
            
            INSTRUCTIONS:
            1. Analyze the user question to understand what attributes/characteristics are being searched for
            2. Examine each user in the context and determine if they match the search criteria
            3. For matching users, extract and return their complete information
            4. Be inclusive - if a user partially matches or could potentially match, include them
            
            OUTPUT FORMAT:
            - If you find matching users: Return their full details exactly as provided, maintaining the original format
            - If no users match: Respond with exactly "NO_MATCHES_FOUND"
            - If uncertain about a match: Include the user with a note about why they might match""";

    private static final String USER_PROMPT = """
            ## USER DATA:
            {context}
            
            ## SEARCH QUERY: 
            {query}""";

    private static final String FINAL_SYSTEM_PROMPT = """
            You are a helpful assistant that provides comprehensive answers based on user search results.
            
            INSTRUCTIONS:
            1. Review all the search results from different user batches
            2. Combine and deduplicate any matching users found across batches
            3. Present the information in a clear, organized manner
            4. If multiple users match, group them logically
            5. If no users match, explain what was searched for and suggest alternatives""";

    ChatClient chatClient;
    UserService userService;
    ObjectMapper objectMapper = new ObjectMapper();

    public DialClient(ChatClient chatClient, UserService userService) {
        this.chatClient = chatClient;
        this.userService = userService;
    }

    public Mono<String> findUserByUserQuery(String message, String conversationId) {
        return userService.getAllUsers()
                .buffer(100)
                .concatMap(userBatch -> chatClient.prompt()
                        .system(BATCH_SYSTEM_PROMPT)
                        .user(userSpec -> userSpec.text(USER_PROMPT)
                                .param("context", toJson(userBatch))
                                .param("query", message))
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .stream()
                        .content())
                .collect(Collectors.joining("\n"))
                .flatMap(content -> {
                    String combinedResponse = content;
                    if (content.contains("NO_MATCHES_FOUND") && !content.trim().equals("NO_MATCHES_FOUND")) {
                        combinedResponse = content.replace("NO_MATCHES_FOUND", "").trim();
                    }
                    final String finalCombinedResponse = combinedResponse;
                    return chatClient.prompt()
                            .system(FINAL_SYSTEM_PROMPT)
                            .user(userSpec -> userSpec.text(USER_PROMPT)
                                    .param("context", finalCombinedResponse)
                                    .param("query", message))
                            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                            .stream()
                            .content()
                            .collect(Collectors.joining());
                });
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
