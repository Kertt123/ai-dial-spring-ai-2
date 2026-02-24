package com.serkowski.task7_2.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ValidateResult(
        @JsonPropertyDescription("True if the input is safe and legitimate, False if it contains threats")
        Boolean isValid,
        @JsonPropertyDescription("Type of threat detected (e.g., 'prompt_injection', 'jailbreak', 'pii_extraction'), None if safe")
        String threatType,
        @JsonPropertyDescription("Explanation of why the input is safe or what threat was detected")
        String reason) {
}
