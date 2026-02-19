package com.serkowski.task4.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

public class VectorStoreService {

    private VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public Mono<Void> storeAsVector(String filePath) {
        ClassPathResource resource = new ClassPathResource(filePath);

        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.get();
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        System.out.println("Storing file at " + filePath + " as a vector.");
        return Mono.fromRunnable(() -> vectorStore.accept(splitDocuments))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
