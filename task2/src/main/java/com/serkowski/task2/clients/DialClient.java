package com.serkowski.task2.clients;

import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    public AssistantMessage getCompletions(List<Message> messages, String deploymentName, int numberOfResponses) {
        String fullText = chatClient.prompt()
                .messages(messages)
                .options(AzureOpenAiChatOptions.builder()
                        .deploymentName(deploymentName)
                        .N(numberOfResponses)
                        .build())
                .call()
                .content();
        return AssistantMessage.builder().content(fullText).build();
    }

    public AssistantMessage getCompletionsStream(List<Message> messages, String deploymentName) {
        String fullText = chatClient.prompt()
                .messages(messages)
                .options(AzureOpenAiChatOptions.builder()
                        .deploymentName(deploymentName)
                        .build())
                .stream()
                .content()
                .map(chunk -> {
                    System.out.print(chunk);
                    return chunk;
                })
                .collect(Collectors.joining())
                .block();
        return AssistantMessage.builder().content(fullText).build();
    }
}
