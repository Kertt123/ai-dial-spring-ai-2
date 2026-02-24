package com.serkowski.task7_4.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record ValidateResult(
        @JsonPropertyDescription("True if the output contains sensitive PII leak, False if safe")
        Boolean containsPiiLeak,
        @JsonPropertyDescription("List of PII types that were leaked (e.g., 'SSN', 'Credit Card', 'Address')")
        List<String> leakedPiiTypes,
        @JsonPropertyDescription("Explanation of what PII was found or why the output is safe")
        String reason) {
}
