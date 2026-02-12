package com.serkowski.task3.model.dial;

import com.serkowski.task3.model.bucket.CustomContent;

public record ResponseMessage(String content, CustomContent custom_content) {
}
