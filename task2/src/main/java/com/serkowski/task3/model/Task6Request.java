package com.serkowski.task3.model;

import java.io.Serializable;

public record Task6Request(String message, double frequencyPenalty) implements Serializable {
}
