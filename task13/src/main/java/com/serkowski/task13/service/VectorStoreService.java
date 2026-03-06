package com.serkowski.task13.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.ClassPathResource;

import java.util.List;
import java.util.stream.Collectors;

public class VectorStoreService {

    private final VectorStore vectorStore;
    private final KeywordMetadataEnricher keywordMetadataEnricher;

    public VectorStoreService(VectorStore vectorStore, KeywordMetadataEnricher keywordMetadataEnricher) {
        this.vectorStore = vectorStore;
        this.keywordMetadataEnricher = keywordMetadataEnricher;
    }

    public void storeAsVector(String filePath) {
        ClassPathResource resource = new ClassPathResource(filePath);
        var exp = new FilterExpressionBuilder();
        var existingDocuments = vectorStore.similaritySearch(SearchRequest.builder()
                        .filterExpression(exp.eq("filename", resource.getFilename()).build())
                        .similarityThreshold(0.1)
                        .topK(1)
                        .build())
                .stream()
                .toList();
        if (!existingDocuments.isEmpty()) {
            return;
        }
        List<Document> documents;
        if (filePath.toLowerCase().endsWith(".pdf")) {
            TikaDocumentReader pdfReader = new TikaDocumentReader(resource);
            documents = pdfReader.get();
            documents.forEach(document -> document.getMetadata().put("filename", resource.getFilename()));
        } else {
            TextReader textReader = new TextReader(resource);
            textReader.getCustomMetadata().put("filename", resource.getFilename());
            documents = textReader.get();
        }

        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);

        keywordMetadataEnricher.apply(splitDocuments);
        System.out.println("Storing file at " + filePath + " as a vector.");
        vectorStore.accept(splitDocuments);
    }
}
