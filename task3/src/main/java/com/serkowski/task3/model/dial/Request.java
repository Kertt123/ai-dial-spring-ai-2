package com.serkowski.task3.model.dial;


import java.util.List;

public record Request(List<RequestMessage> messages, boolean stream) {
}
