package com.serkowski.task14.generalPurpose.model.dial;


import com.serkowski.task14.generalPurpose.model.bucket.CustomContent;

public record ResponseMessage(String content, CustomContent custom_content) {
}
