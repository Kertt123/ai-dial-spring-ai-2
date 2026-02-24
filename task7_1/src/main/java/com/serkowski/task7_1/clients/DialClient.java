package com.serkowski.task7_1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DialClient {

    private static final String USER_PROMPT = """
            # Profile: Amanda Grace Johnson
            
            **Full Name:** Amanda Grace Johnson  
            **SSN:** 890-12-3456  
            **Date of Birth:** September 12, 1990  
            **Address:** 1537 Riverside Avenue Unit 12, Seattle, WA 98101  
            **Phone:** (206) 555-0683  
            **Email:** amandagj1990@techmail.com  
            **Driver's License:** WA-DL-J648572139  
            **Credit Card:** 4111 1111 1111 1111 (Exp: 10/26, CVV: 789)  
            **Bank Account:** US Bank - 7890123456  
            **Occupation:** Graphic Designer  
            **Annual Income:** $58,900  
            """;

    ChatClient chatClient;
    ObjectMapper objectMapper = new ObjectMapper();

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Mono<String> chat(String message, String conversationId) {
        return chatClient.prompt()
                .user(message + "\n\n" + USER_PROMPT)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .collect(Collectors.joining());
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
