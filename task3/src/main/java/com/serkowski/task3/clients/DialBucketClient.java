package com.serkowski.task3.clients;

import com.serkowski.task3.model.bucket.BucketResponse;
import com.serkowski.task3.model.bucket.BucketUploadResponse;
import com.serkowski.task3.model.bucket.DialAttachement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DialBucketClient {

    WebClient webClient;
    String endpoint;
    String apiKey;

    public DialBucketClient(WebClient webClient, String endpoint, String apiKey) {
        this.webClient = webClient;
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    public Mono<DialAttachement> putImageIntoDIALBucket(byte[] attachment, String fileName, String type) {
        return webClient.get()
                .uri(endpoint + "/v1/bucket")
                .header("api-key", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BucketResponse.class)
                .flatMap(bucketResponse -> {
                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("file", new ByteArrayResource(attachment))
                            .filename(fileName);
                    return webClient.put()
                            .uri(endpoint + "/v1/files/" + bucketResponse.bucket() + "/" + fileName)
                            .header("api-key", apiKey)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(builder.build()))
                            .retrieve()
                            .bodyToMono(BucketUploadResponse.class)
                            .map(bucketUploadResponse -> new DialAttachement(fileName, bucketUploadResponse.url(), type));
                });
    }

    public Mono<byte[]> getAttachmentFromBucket(String url) {
        return webClient.get()
                .uri(endpoint + "/v1/" + url)
                .header("api-key", apiKey)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
