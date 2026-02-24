package com.serkowski.task7_3.clients;

import com.serkowski.task7_3.model.ValidateResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Service
public class DialClient {

    private static final String VALIDATION_PROMPT = """
            You are a PII (Personally Identifiable Information) leak detector. Analyze the LLM output and determine if it contains any sensitive PII that should not be disclosed.
            
            ALLOWED INFORMATION (NOT a leak):
            - Full Name
            - Phone number
            - Email address
            
            SENSITIVE PII (IS a leak - must be detected):
            - SSN (Social Security Number)
            - Date of Birth
            - Home Address
            - Driver's License number
            - Credit Card number, expiration date, CVV
            - Bank Account number
            - Annual Income / Salary
            - Any other financial or identity documents
            
            Analyze the following LLM output for PII leaks:
            
            LLM OUTPUT:
            {llm_output}
            """;

    private final static String FILTER_SYSTEM_PROMPT = """
                You are a PII filter. Your task is to take the given text and remove or redact any sensitive PII while preserving allowed information.
            
            KEEP (do not redact):
            - Full Name
            - Phone number  
            - Email address
            
            REDACT (replace with [REDACTED]):
            - SSN -> [REDACTED SSN]
            - Date of Birth -> [REDACTED DOB]
            - Home Address -> [REDACTED ADDRESS]
            - Driver's License -> [REDACTED LICENSE]
            - Credit Card numbers, CVV, expiration -> [REDACTED CARD]
            - Bank Account -> [REDACTED BANK]
            - Annual Income -> [REDACTED INCOME]
            
            Return the filtered text with sensitive PII redacted. Keep the message natural and coherent.
            
            TEXT TO FILTER:
            {text}
            """;

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Mono<String> chat(String message, String conversationId, boolean softResponse) {
        return Mono.fromCallable(() -> chatClient.prompt()
                        .user(message)
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .call()
                        .content())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(response -> Mono.fromCallable(() -> chatClient.prompt()
                                .user(userSpec -> userSpec
                                        .text(VALIDATION_PROMPT)
                                        .param("llm_output", response))
                                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                                .call()
                                .entity(ValidateResult.class))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(validateResult -> {
                            if (validateResult.containsPiiLeak()) {
                                if (softResponse) {
                                    return chatClient.prompt()
                                            .user(userSpec -> userSpec.text(FILTER_SYSTEM_PROMPT)
                                                    .param("text", response))
                                            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                                            .stream()
                                            .content()
                                            .collect(Collectors.joining());
                                } else {
                                    return Mono.just("Response blocked due to detected PII leak! Detected sensitive information: " + String.join(", ", validateResult.leakedPiiTypes()));
                                }
                            } else {
                                return Mono.just(response);
                            }
                        }));
    }

}
