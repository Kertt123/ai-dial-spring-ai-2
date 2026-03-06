package com.serkowski.task13.model.dial;

public record ResponseChoice(ResponseMessage message, ResponseDelta delta, String finish_reason) {
}
