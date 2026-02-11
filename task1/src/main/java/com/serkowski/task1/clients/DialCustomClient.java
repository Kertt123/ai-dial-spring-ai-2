package com.serkowski.task1.clients;

import com.serkowski.task1.model.Request;
import com.serkowski.task1.model.RequestMessage;
import com.serkowski.task1.model.Response;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class DialCustomClient {

    WebClient webClient;
    String url;
    String apiKey;

    public DialCustomClient(WebClient webClient, String url, String apiKey) {
        this.webClient = webClient;
        this.url = url;
        this.apiKey = apiKey;
    }


    public AssistantMessage getCompletions(List<Message> messages, String deploymentName) {
        System.out.println("[CustomClient] REQUEST:");
        Request request = new Request(messages.stream()
                .map(message -> new RequestMessage(message.getMessageType().getValue(), message.getText()))
                .toList(), false);
        System.out.println(request);
        Response response = webClient.post().uri(url + "/openai/deployments/" + deploymentName + "/chat/completions")
                .bodyValue(request)
                .header("api-key", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> {
                    System.out.println("[CustomClient] RESPONSE STATUS: " + clientResponse.statusCode());
                    return clientResponse.bodyToMono(Response.class);
                })
                .block();
        System.out.println("[CustomClient] RESPONSE BODY:" + response);
        return new AssistantMessage(response.choices().stream().findFirst().get().message().content());
    }

    public AssistantMessage getCompletionsStream(List<Message> messages, String deploymentName) {
        System.out.println("[CustomClient] REQUEST:");

        Request request = new Request(messages.stream()
                .map(message -> new RequestMessage(message.getMessageType().getValue(), message.getText()))
                .toList(), true);

        return webClient
                .post()
                .uri(url + "/openai/deployments/" + deploymentName + "/chat/completions")
                .header("api-key", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
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
                .map(contents -> {
                    System.out.println("");
                    return new AssistantMessage(String.join("", contents));
                })
                .block();
    }
}
