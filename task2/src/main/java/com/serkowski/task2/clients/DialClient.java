package com.serkowski.task2.clients;

import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    public Mono<String> getCompletions(List<Message> messages, String deploymentName, int numberOfResponses) {
        return chatClient.prompt()
                .messages(messages)
                .options(AzureOpenAiChatOptions.builder()
                        .deploymentName(deploymentName)
                        .N(numberOfResponses)
                        .build())
                .stream()
                .content()
                .collect(Collectors.joining());
    }
}
