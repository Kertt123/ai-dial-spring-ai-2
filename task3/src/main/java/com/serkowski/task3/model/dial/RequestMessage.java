package com.serkowski.task3.model.dial;

import com.serkowski.task3.model.bucket.CustomContent;

import java.io.Serializable;

public record RequestMessage(String role, String content, CustomContent custom_content) implements Serializable {
}
