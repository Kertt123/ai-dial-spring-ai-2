package com.serkowski.clients;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DialClient {

    ChatModel chatModel;
    StreamingChatModel streamingChatModel;

    public DialClient(ChatModel chatModel, StreamingChatModel streamingChatModel) {
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
    }


    public AssistantMessage getCompletions(List<Message> message, String deploymentName) {
        return Objects.requireNonNull(chatModel.call(Prompt.builder()
                .messages(message)
                .chatOptions(ChatOptions.builder()
                        .model(deploymentName)
                        .build())
                .build()).getResult()).getOutput();
    }

    public AssistantMessage getCompletionsStream(List<Message> message, String deploymentName) {
        Flux<ChatResponse> fluxResponse = streamingChatModel.stream(Prompt.builder()
                .messages(message)
                .chatOptions(ChatOptions.builder()
                        .model(deploymentName)
                        .build())
                .build());
        String fullText = fluxResponse
                .map(ChatResponse::getResult)
                .map(result -> {
                    if (result.getOutput().getText() != null) {
                        System.out.print(result.getOutput().getText());
                        return result.getOutput().getText();
                    }
                    return "";
                })
                .collect(Collectors.joining())
                .block();
        return AssistantMessage.builder().content(fullText).build();
    }
}
