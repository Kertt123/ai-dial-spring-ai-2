package com.serkowski.task12.model.bucket;

import com.serkowski.task12.model.bucket.DialAttachement;

import java.io.Serializable;
import java.util.List;

public record CustomContent(List<DialAttachement> attachments) implements Serializable {
}
