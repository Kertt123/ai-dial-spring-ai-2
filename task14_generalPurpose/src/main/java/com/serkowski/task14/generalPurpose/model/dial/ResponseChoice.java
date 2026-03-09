package com.serkowski.task14.generalPurpose.model.dial;

public record ResponseChoice(ResponseMessage message, ResponseDelta delta, String finish_reason) {
}
