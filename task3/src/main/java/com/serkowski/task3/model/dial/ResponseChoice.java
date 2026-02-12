package com.serkowski.task3.model.dial;

public record ResponseChoice(ResponseMessage message, ResponseDelta delta, String finish_reason) {
}
