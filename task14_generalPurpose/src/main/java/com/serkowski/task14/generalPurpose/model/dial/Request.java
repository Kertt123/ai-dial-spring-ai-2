package com.serkowski.task14.generalPurpose.model.dial;


import java.util.List;

public record Request(List<RequestMessage> messages, boolean stream) {
}
