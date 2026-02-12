package com.serkowski.task3.model.bucket;

import java.io.Serializable;
import java.util.List;

public record CustomContent(List<DialAttachement> attachments) implements Serializable {
}
