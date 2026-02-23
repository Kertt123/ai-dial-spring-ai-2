package com.serkowski.task6_4.clients;

import com.serkowski.task6_4.model.User;
import com.serkowski.task6_4.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class DialClient {
    private final static String EXTRACTION_SYSTEM_PROMPT = """
                You are a Named Entity Extraction assistant. Your task is to analyze user profiles and extract user IDs that match the requested hobbies.
            
            INSTRUCTIONS:
            1. Analyze each user's 'about_me' section
            2. Identify hobbies and interests mentioned
            3. Group users by their hobbies that match the query
            4. Return ONLY user IDs, not full user information
            5. A user can appear in multiple hobby categories
            """;

    private final ChatClient chatClient;
    private final UserService userService;

    public DialClient(ChatClient chatClient, UserService userService) {
        this.chatClient = chatClient;
        this.userService = userService;
    }

    public Flux<User> findUserByUserQuery(String message, String conversationId) {
        Mono<List<Integer>> userIdsMono = Mono.fromCallable(() -> chatClient.prompt()
                        .system(EXTRACTION_SYSTEM_PROMPT)
                        .user(message)
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .call()
                        .entity(new ParameterizedTypeReference<List<Integer>>() {
                        }))
                .subscribeOn(Schedulers.boundedElastic());

        return userIdsMono
                .flatMapMany(Flux::fromIterable)
                .flatMap(userId ->
                        userService.getUserById(String.valueOf(userId))
                                .onErrorResume(e -> {
                                    System.err.println("Error fetching user " + userId + ": " + e.getMessage());
                                    return Mono.empty();
                                })
                );
    }

}
