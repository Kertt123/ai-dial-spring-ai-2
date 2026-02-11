package com.serkowski.task2.model;

import java.io.Serializable;

public record Task2Request(String message, int n) implements Serializable {
}
