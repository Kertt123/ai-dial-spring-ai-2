package com.serkowski.model;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public record RequestMessage(String role, String content) {
}
