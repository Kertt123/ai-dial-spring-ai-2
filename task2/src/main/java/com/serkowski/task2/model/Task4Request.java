package com.serkowski.task2.model;

import java.io.Serializable;

public record Task4Request(String message, long seed) implements Serializable {
}
