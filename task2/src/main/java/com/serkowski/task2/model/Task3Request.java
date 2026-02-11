package com.serkowski.task2.model;

import java.io.Serializable;

public record Task3Request(String message, double temperature) implements Serializable {
}
