package com.serkowski.task13.service;

import com.serkowski.task13.model.bucket.DialAttachement;
import com.serkowski.task13.model.dial.Configuration;
import com.serkowski.task13.model.dial.CustomField;
import com.serkowski.task13.model.dial.Request;
import com.serkowski.task13.model.dial.RequestMessage;
import com.serkowski.task13.model.dial.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

public class ImageGenerationService {

    private final String url;
    private final String apiKey;
    private final DialBucketClient dialBucketClient;
    private final RestTemplate restTemplate;

    public ImageGenerationService(String url, String apiKey, DialBucketClient dialBucketClient, RestTemplate restTemplate) {
        this.url = url;
        this.apiKey = apiKey;
        this.dialBucketClient = dialBucketClient;
        this.restTemplate = restTemplate;
    }

    public byte[] generateImage(String prompt, String size, String style, String quality) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Request requestBody = new Request(List.of(new RequestMessage("user", prompt, null, new CustomField(new Configuration(size, style, quality)))), false);

        try {
            ResponseEntity<Response> responseEntity = restTemplate.exchange(
                    url + "/openai/deployments/dall-e-3/chat/completions",
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    Response.class
            );

            Response response = responseEntity.getBody();
            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new RuntimeException("Empty response from AI service");
            }

            String imageUrl = response.choices().iterator().next().message().custom_content().attachments().stream()
                    .map(DialAttachement::url)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Image URL not found in response"));

            return dialBucketClient.getAttachmentFromBucket(imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}