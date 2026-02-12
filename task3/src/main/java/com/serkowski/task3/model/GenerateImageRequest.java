package com.serkowski.task3.model;

public record GenerateImageRequest(
        String content,
        String size,
        String style,
        String quality
) {
}

