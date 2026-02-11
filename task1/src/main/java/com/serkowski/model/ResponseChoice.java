package com.serkowski.model;

public record ResponseChoice(ResponseMessage message, ResponseDelta delta, String finish_reason) {
}
