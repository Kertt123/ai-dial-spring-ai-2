package com.serkowski.task12.model.dial;

import com.serkowski.task12.model.bucket.CustomContent;

public record ResponseMessage(String content, CustomContent custom_content) {
}
