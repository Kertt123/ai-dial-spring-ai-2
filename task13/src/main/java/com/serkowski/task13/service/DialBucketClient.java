package com.serkowski.task13.service;

import com.serkowski.task13.model.bucket.BucketResponse;
import com.serkowski.task13.model.bucket.BucketUploadResponse;
import com.serkowski.task13.model.bucket.DialAttachement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class DialBucketClient {

    private final RestTemplate restTemplate;
    private final String endpoint;
    private final String apiKey;

    public DialBucketClient(RestTemplate restTemplate, String endpoint, String apiKey) {
        this.restTemplate = restTemplate;
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    public DialAttachement putImageIntoDIALBucket(byte[] attachment, String fileName, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<BucketResponse> bucketResponse = restTemplate.exchange(
                endpoint + "/v1/bucket",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BucketResponse.class
        );

        if (bucketResponse.getBody() == null) {
            throw new RuntimeException("Bucket response is empty");
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(attachment) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.set("api-key", apiKey);
        uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<BucketUploadResponse> uploadResponse = restTemplate.exchange(
                endpoint + "/v1/files/" + bucketResponse.getBody().bucket() + "/" + fileName,
                HttpMethod.PUT,
                new HttpEntity<>(body, uploadHeaders),
                BucketUploadResponse.class
        );

        if (uploadResponse.getBody() == null) {
            throw new RuntimeException("Upload response is empty");
        }

        return new DialAttachement(fileName, uploadResponse.getBody().url(), type);
    }

    public byte[] getAttachmentFromBucket(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                endpoint + "/v1/" + url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                byte[].class
        );

        return response.getBody();
    }
}
