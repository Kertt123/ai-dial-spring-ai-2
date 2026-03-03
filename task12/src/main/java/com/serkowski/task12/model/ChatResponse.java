package com.serkowski.task12.model;

import java.util.Map;

public record ChatResponse(String response, Map<String, String> toolResponse) {
}
