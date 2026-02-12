package com.serkowski.task3.clients;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Mono<String> getCompletionsWithImageUrl(String message, String imgType, String imageUrl) {
        return Mono.defer(() -> {
            try {
                UrlResource resource = new UrlResource(imageUrl);
                return chatClient.prompt()
                        .user(userSpec -> userSpec
                                .text(message)
                                .media(MimeType.valueOf(imgType), resource))
                        .stream()
                        .content()
                        .collect(Collectors.joining());
            } catch (Exception e) {
                return Mono.error(new IllegalArgumentException("Not correct image URL " + imageUrl, e));
            }
        });
    }

    public Mono<String> getCompletionsWithImagePath(String message, String imgType, String imgPath) {
        return Mono.defer(() -> {
            try {
                ClassPathResource resource = new ClassPathResource(imgPath);
                return chatClient.prompt()
                        .user(userSpec -> userSpec
                                .text(message)
                                .media(MimeType.valueOf(imgType), resource))
                        .stream()
                        .content()
                        .collect(Collectors.joining());
            } catch (Exception e) {
                return Mono.error(new IllegalArgumentException("Not correct image path " + imgPath, e));
            }
        });
    }
}
