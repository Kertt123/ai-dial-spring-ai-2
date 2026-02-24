package com.serkowski.task7_4.clients;

import com.serkowski.task7_4.service.TextRedactor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DialClient {

    private final ChatClient chatClient;
    private final TextRedactor textRedactor;

    public DialClient(ChatClient chatClient, TextRedactor textRedactor) {
        this.chatClient = chatClient;
        this.textRedactor = textRedactor;
    }

    /**
     * Original streaming chat — collects all chunks into a single response.
     */
    public Mono<String> chat(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .collect(Collectors.joining());
    }

    /**
     * "Shrek" approach — buffers ALL chunks from the stream, joins them,
     * then redacts sensitive PII before returning the full text.
     */
    public Mono<String> chatWithRedaction(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .collect(Collectors.joining())       // buffer all chunks into one String
                .map(textRedactor::redact);           // redact PII from the buffered result
    }

    /**
     * Streaming variant with overlap buffer to prevent PII split across chunk boundaries.
     * <p>
     * Each window keeps an overlap (tail) of {@link TextRedactor#MAX_PII_LENGTH} characters
     * from the previous window. The overlap region is redacted together with the new text,
     * but only the NEW portion is emitted — so no PII pattern can slip through a boundary.
     * On completion, the remaining tail is flushed and redacted.
     */
    public Flux<String> chatWithStreamingRedaction(String message, String conversationId, int bufferSize) {
        final int overlap = TextRedactor.MAX_PII_LENGTH;

        Flux<String> windows = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .buffer(Math.max(bufferSize, overlap))
                .map(chunks -> String.join("", chunks));

        return Flux.create(sink -> {
            final StringBuilder carry = new StringBuilder();

            windows.subscribe(
                    window -> {
                        carry.append(window);

                        if (carry.length() <= overlap) {
                            // Not enough text yet — just accumulate, don't emit
                            return;
                        }

                        // Split: [safe-to-emit region] [overlap tail kept for next window]
                        String safeRegion = carry.substring(0, carry.length() - overlap);
                        String tail = carry.substring(carry.length() - overlap);

                        carry.setLength(0);
                        carry.append(tail);

                        sink.next(textRedactor.redact(safeRegion));
                    },
                    sink::error,
                    () -> {
                        // Flush remaining carry
                        if (!carry.isEmpty()) {
                            sink.next(textRedactor.redact(carry.toString()));
                        }
                        sink.complete();
                    }
            );
        });
    }
}
