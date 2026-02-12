package com.serkowski.task3.model;

import java.io.Serializable;

public record Task4Request(String message, long seed) implements Serializable {
}
