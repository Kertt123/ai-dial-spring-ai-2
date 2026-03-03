package com.serkowski.task12.model.dial;


import com.serkowski.task12.model.dial.RequestMessage;

import java.util.List;

public record Request(List<RequestMessage> messages, boolean stream) {
}
