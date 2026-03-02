package com.serkowski.task11.model;

public record ConversationSummary(String id, String title, String createdAt, String updatedAt, int messageCount) {
}