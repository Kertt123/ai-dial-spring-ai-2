package com.serkowski.task14.generalPurpose.tools;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FileContentExtractor {


    public static String extractContent(ClassPathResource resource) throws IOException {
        File file = resource.getFile();
        if (file.getName().endsWith(".txt")) {
            TextReader textReader = new TextReader(resource);
            return extractText(textReader.get());
        } else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".html") || file.getName().endsWith(".htm")) {
            TikaDocumentReader pdfReader = new TikaDocumentReader(resource);
            return extractText(pdfReader.get());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + file.getName());
        }
    }

    private static @NonNull String extractText(List<Document> documents) {
        return documents.stream().map(Document::getText).collect(Collectors.joining());
    }
}
