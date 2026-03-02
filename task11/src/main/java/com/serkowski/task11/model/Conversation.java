package com.serkowski.task11.model;

import java.util.List;

public record Conversation(String id, String title, List<MessageResponse> messages) {
}