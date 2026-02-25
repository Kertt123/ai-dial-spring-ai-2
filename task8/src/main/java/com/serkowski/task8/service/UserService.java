package com.serkowski.task8.service;

import com.serkowski.task8.model.SearchRequest;
import com.serkowski.task8.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class UserService {

    private static final String USER_SERVICE_ENDPOINT = "http://localhost:8041";

    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        return restTemplate.exchange(
                USER_SERVICE_ENDPOINT + "/v1/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        ).getBody();
    }

    public User getUserById(String id) {
        return restTemplate.getForObject(USER_SERVICE_ENDPOINT + "/v1/users/{id}", User.class, id);
    }

    public List<User> searchUsers(SearchRequest searchRequest) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(USER_SERVICE_ENDPOINT + "/v1/users/search");

        if (searchRequest.name() != null) builder.queryParam("name", searchRequest.name());
        if (searchRequest.surname() != null) builder.queryParam("surname", searchRequest.surname());
        if (searchRequest.email() != null) builder.queryParam("email", searchRequest.email());
        if (searchRequest.gender() != null) builder.queryParam("gender", searchRequest.gender());

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        ).getBody();
    }

    public User createUser(User user) {
        return restTemplate.postForObject(USER_SERVICE_ENDPOINT + "/v1/users", user, User.class);
    }

    public User updateUser(int id, User user) {
        return restTemplate.exchange(
                USER_SERVICE_ENDPOINT + "/v1/users/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(user),
                User.class,
                id
        ).getBody();
    }

    public void deleteUser(int id) {
        restTemplate.delete(USER_SERVICE_ENDPOINT + "/v1/users/{id}", id);
    }
}
