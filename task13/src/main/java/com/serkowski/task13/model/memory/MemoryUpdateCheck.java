package com.serkowski.task13.model.memory;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record MemoryUpdateCheck(
        @JsonPropertyDescription("Whether the conversation contains NEW information about the user that should be stored")
        Boolean shouldUpdate,
        @JsonPropertyDescription("Brief 1-sentence explanation of why update is/isn't needed")
        String reason) {
}
