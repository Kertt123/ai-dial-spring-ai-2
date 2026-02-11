package com.serkowski.task2.model;

import java.io.Serializable;

public record Task7Request(String message, double presencePenalty) implements Serializable {
}
