package com.serkowski.task14.generalPurpose.tools;

import com.serkowski.generalPurpose.model.ChatResponse;
import com.serkowski.generalPurpose.service.ImageGenerationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class ImageGenerationTool {

    private final ImageGenerationService imageGenerationService;

    public ImageGenerationTool(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    @Tool(name = "generate_image", description = "Generates an image based on a textual description. Use this tool when the user asks to create, draw, or generate an image.", returnDirect = true)
    public ChatResponse generateImage(String prompt) {
        byte[] bytes = imageGenerationService.generateImage(prompt, "1024x1024", "vivid", "standard");
        return new ChatResponse("", Map.of("encodedImage", Base64.getEncoder().encodeToString(bytes)));
    }
}
