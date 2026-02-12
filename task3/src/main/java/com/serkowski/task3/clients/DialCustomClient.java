package com.serkowski.task3.clients;

import com.serkowski.task3.model.bucket.CustomContent;
import com.serkowski.task3.model.bucket.DialAttachement;
import com.serkowski.task3.model.dial.Request;
import com.serkowski.task3.model.dial.RequestMessage;
import com.serkowski.task3.model.dial.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class DialCustomClient {

    WebClient webClient;
    String url;
    String apiKey;
    DialBucketClient dialBucketClient;

    public DialCustomClient(WebClient webClient, String url, String apiKey, DialBucketClient dialBucketClient) {
        this.webClient = webClient;
        this.url = url;
        this.apiKey = apiKey;
        this.dialBucketClient = dialBucketClient;
    }

    public Mono<String> getCompletionsWithImagePathDIAL(String message, String imgType, String imgPath) {
        return Mono.defer(() -> {
            try {
                ClassPathResource resource = new ClassPathResource(imgPath);
                byte[] contentAsByteArray = resource.getContentAsByteArray();
                return dialBucketClient.putImageIntoDIALBucket(contentAsByteArray, resource.getFilename(), imgType)
                        .flatMap(dialAttachement ->
                                getCompletionsStream(message, dialAttachement)
                        );
            } catch (Exception e) {
                return Mono.error(new IllegalArgumentException("Not correct image path " + imgPath, e));
            }
        });
    }

    public Mono<String> getCompletionsStream(String message, DialAttachement dialAttachement) {
        return webClient
                .post()
                .uri(url + "/openai/deployments/gpt-4o/chat/completions")
                .header("api-key", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(new Request(List.of(new RequestMessage("user", message, new CustomContent(List.of(dialAttachement)))), true))
                .retrieve()
                .bodyToFlux(Response.class)
                .takeWhile(response -> {
                    String finish_reason = response.choices().stream().findFirst().get().finish_reason();
                    return finish_reason == null || !finish_reason.equals("stop");
                })
                .map(chunkData -> {
                    try {
                        String content = chunkData.choices().stream().findFirst().get().delta().content();
                        System.out.print(content);
                        return content;
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(content -> !content.isEmpty())
                .collectList()
                .map(contents -> String.join("", contents));
    }
}
