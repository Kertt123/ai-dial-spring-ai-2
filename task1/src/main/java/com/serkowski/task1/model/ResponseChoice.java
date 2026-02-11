package com.serkowski.task1.model;

public record ResponseChoice(ResponseMessage message, ResponseDelta delta, String finish_reason) {
}
