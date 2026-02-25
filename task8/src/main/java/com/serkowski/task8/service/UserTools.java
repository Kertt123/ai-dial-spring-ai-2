package com.serkowski.task8.service;

import com.serkowski.task8.model.SearchRequest;
import com.serkowski.task8.model.User;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

public class UserTools {

    private final UserService userService;

    public UserTools(UserService userService) {
        this.userService = userService;
    }

    @Tool(description = """
            Creates a new user record in the directory service.
            Required fields: name, surname, email, about_me,
            The rest of the fields are optional, for the objects like address and credit_card set null if they are not provided.
            """)
    public User createUser(@ToolParam(description = "User specification. Set all unmentioned fields to null.") User user) {
        try {
            return userService.createUser(user);
        } catch (RestClientResponseException e) {
            throw logAndRethrow(e);
        }
    }

    @Tool(description = """
            Updates an existing user record in the directory service.
            Required fields: id,
            The rest of the fields are optional, for the objects like address and credit_card set null if they are not provided.
            """)
    public User updateUser(@ToolParam(description = "User specification. Set all unmentioned fields to null.") User user) {
        try {
            return userService.updateUser(user.id(), user);
        } catch (RestClientResponseException e) {
            throw logAndRethrow(e);
        }
    }

    @Tool(description = "Delete existing user by id.")
    public void deleteUserById(@ToolParam(description = "User id") int userId) {
        try {
            userService.deleteUser(userId);
        } catch (RestClientResponseException e) {
            throw logAndRethrow(e);
        }
    }

    @Tool(description = """
            Search user by name, surname, email and gender. All parameters are optional, but at least one must be provided.
            """)
    public List<User> searchUser(@ToolParam(description = "Search specification") SearchRequest searchRequest) {
        try {
            return userService.searchUsers(searchRequest);
        } catch (RestClientResponseException e) {
            throw logAndRethrow(e);
        }
    }

    @Tool(description = """
            Get user details by id. Returns 404 if user with provided id does not exist.
            """)
    public User getUserById(@ToolParam(description = "User id") String userId) {
        try {
            return userService.getUserById(userId);
        } catch (RestClientResponseException e) {
            throw logAndRethrow(e);
        }
    }

    private static RuntimeException logAndRethrow(RestClientResponseException e) {
        System.err.println("HTTP Status  : " + e.getStatusCode());
        System.err.println("Response body: " + e.getResponseBodyAsString());
        return e;
    }
}
