package com.serkowski.task12.model.dial;

import com.serkowski.task12.model.dial.ResponseMessage;

public record ResponseChoice(ResponseMessage message, ResponseDelta delta, String finish_reason) {
}
