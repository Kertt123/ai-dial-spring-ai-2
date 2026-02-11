package com.serkowski.model;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public record Request(List<RequestMessage> messages, boolean stream) {
}
