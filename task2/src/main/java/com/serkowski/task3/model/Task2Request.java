package com.serkowski.task3.model;

import java.io.Serializable;

public record Task2Request(String message, int n) implements Serializable {
}
