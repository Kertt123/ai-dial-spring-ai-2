package com.serkowski.task13.model.dial;

import com.serkowski.task13.model.bucket.CustomContent;

public record ResponseMessage(String content, CustomContent custom_content) {
}
