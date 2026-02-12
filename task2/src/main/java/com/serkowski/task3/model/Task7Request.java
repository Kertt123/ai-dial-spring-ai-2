package com.serkowski.task3.model;

import java.io.Serializable;

public record Task7Request(String message, double presencePenalty) implements Serializable {
}
