package com.serkowski.task14.generalPurpose.model.bucket;

import java.io.Serializable;
import java.util.List;

public record CustomContent(List<DialAttachement> attachments) implements Serializable {
}
