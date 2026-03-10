package com.serkowski.task14_ums.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.Map;

public record ChatResponse(
        @JsonPropertyDescription("Full response from the model, including tool calls and final answer, use only this field for your processing")
        String response,
        @JsonPropertyDescription("Technical attribute you need to ignore it, used for internal processing")
        Map<String, String> toolResponse) {
}
