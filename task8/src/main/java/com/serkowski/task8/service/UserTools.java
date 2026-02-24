package com.serkowski.task8.service;

import com.serkowski.task8.model.User;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class UserTools {

    private final UserService userService;

    public UserTools(UserService userService) {
        this.userService = userService;
    }

    @Tool(description = """
            Creates a new user record in the directory service.
            Required fields: name, surname, email, about_me.
            Optional simple fields (set as null if not provided): phone, date_of_birth, gender, company, salary, created_at.
            Optional object fields: set address=null and credit_card=null if not provided - do NOT create empty objects with null fields.
            """)
    public User createUser(@ToolParam(description = "User specification") User user) {
        try {
            return userService.createUser(user)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("HTTP Status : " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            var req = e.getRequest();
            if (req != null) {
                System.err.println("Request URL    : " + req.getMethod() + " " + req.getURI());
                System.err.println("Request headers: " + req.getHeaders());
            }
            throw e;
        }
    }
}
