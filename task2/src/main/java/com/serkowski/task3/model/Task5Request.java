package com.serkowski.task3.model;

import java.io.Serializable;

public record Task5Request(String message, int maxTokens) implements Serializable {
}
