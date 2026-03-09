package com.serkowski.task14.generalPurpose.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.ClassPathResource;

public class FileExtractor {

    @Tool(description = """
            Extracts text content from files. Supported formats: PDF (text only), TXT, CSV (as a markdown table), HTML/HTM.
            For files larger than 10,000 characters, pagination is enabled. 
            The response for a paginated file will include '**Page #X. Total pages: Y**' at the end. 
            To navigate through pages, use the 'page' parameter, starting with page=1 by default.
            """)
    public String extractTextContent(String filePath, int page) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (!resource.exists()) {
                return "File not found: " + filePath;
            }
            String content = FileContentExtractor.extractContent(resource);
            if (content.length() <= 10000) {
                return content;
            } else {
                int totalPages = (int) Math.ceil((double) content.length() / 10000);
                if (page < 1 || page > totalPages) {
                    return "Invalid page number. Please provide a page number between 1 and " + totalPages + ".";
                }
                int start = (page - 1) * 10000;
                int end = Math.min(start + 10000, content.length());
                return content.substring(start, end) + "\n\n**Page #" + page + ". Total pages: " + totalPages + "**";
            }
        } catch (Exception e) {
            return "Error extracting content: " + e.getMessage();
        }
    }
}
