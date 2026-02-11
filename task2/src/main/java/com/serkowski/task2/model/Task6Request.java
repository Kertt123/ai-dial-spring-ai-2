package com.serkowski.task2.model;

import java.io.Serializable;

public record Task6Request(String message, double frequencyPenalty) implements Serializable {
}
