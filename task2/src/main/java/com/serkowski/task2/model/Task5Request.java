package com.serkowski.task2.model;

import java.io.Serializable;

public record Task5Request(String message, int maxTokens) implements Serializable {
}
