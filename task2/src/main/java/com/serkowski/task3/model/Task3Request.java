package com.serkowski.task3.model;

import java.io.Serializable;

public record Task3Request(String message, double temperature) implements Serializable {
}
