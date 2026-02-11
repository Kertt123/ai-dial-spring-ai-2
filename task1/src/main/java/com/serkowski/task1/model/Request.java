package com.serkowski.task1.model;

import java.util.List;

public record Request(List<RequestMessage> messages, boolean stream) {
}
