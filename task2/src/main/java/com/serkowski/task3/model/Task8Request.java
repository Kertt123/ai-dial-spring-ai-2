package com.serkowski.task3.model;

import java.io.Serializable;
import java.util.List;

public record Task8Request(String message, List<String> stop) implements Serializable {
}
