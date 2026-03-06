package com.serkowski.task13.model.memory;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record MemoryUpdateResult(
        @JsonPropertyDescription("The complete updated memory content in concise bullet point format with all existing + new information")
        String updatedMemory) {
}
