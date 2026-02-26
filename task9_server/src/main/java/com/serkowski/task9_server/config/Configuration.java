package com.serkowski.task9_server.config;

import com.serkowski.task9_server.service.UserService;
import com.serkowski.task9_server.service.UserTools;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {

    private final String SEARCH_ASSIST_PROMPT = """
            You are helping users search through a dynamic user database. The database contains 
            realistic synthetic user profiles with the following searchable fields:
            
            ## Available Search Parameters
            - **name**: First name (partial matching, case-insensitive)
            - **surname**: Last name (partial matching, case-insensitive)  
            - **email**: Email address (partial matching, case-insensitive)
            - **gender**: Exact match (male, female, other, prefer_not_to_say)
            
            ## Search Strategy Guidance
            
            ### For Name Searches
            - Use partial names: "john" finds John, Johnny, Johnson, etc.
            - Try common variations: "mike" vs "michael", "liz" vs "elizabeth"
            - Consider cultural name variations
            
            ### For Email Searches  
            - Search by domain: "gmail" for all Gmail users
            - Search by name patterns: "john" for emails containing john
            - Use company names to find business emails
            
            ### For Demographic Analysis
            - Combine gender with other criteria for targeted searches
            - Use broad searches first, then narrow down
            
            ### Effective Search Combinations
            - Name + Gender: Find specific demographic segments
            - Email domain + Surname: Find business contacts
            - Partial names: Cast wider nets for common names
            
            ## Example Search Patterns
            ```
            "Find all Johns" → name="john"
            "Gmail users named Smith" → email="gmail" + surname="smith"  
            "Female users with company emails" → gender="female" + email="company"
            "Users with Johnson surname" → surname="johnson"
            ```
            
            ## Tips for Better Results
            1. Start broad, then narrow down
            2. Try variations of names (John vs Johnny)
            3. Use partial matches creatively
            4. Combine multiple criteria for precision
            5. Remember searches are case-insensitive
            
            When helping users search, suggest multiple search strategies and explain 
            why certain approaches might be more effective for their goals.
            """;


    private final String PROFILE_CREATION_PROMPT = """
            You are helping create realistic user profiles for the system. Follow these guidelines 
            to ensure data consistency and realism.
            
            ## Required Fields
            - **name**: 2-50 characters, letters only, culturally appropriate
            - **surname**: 2-50 characters, letters only  
            - **email**: Valid format, must be unique in system
            - **about_me**: Rich, realistic biography (see guidelines below)
            
            ## Optional Fields Best Practices
            - **phone**: Use E.164 format (+1234567890) when possible
            - **date_of_birth**: YYYY-MM-DD format, realistic ages (18-80)
            - **gender**: Use standard values (male, female, other, prefer_not_to_say)
            - **company**: Real-sounding company names
            - **salary**: $30,000-$200,000 range for employed individuals
            
            ## Address Guidelines
            Provide complete, realistic addresses:
            - **country**: Full country names
            - **city**: Actual city names  
            - **street**: Realistic street addresses
            - **flat_house**: Apartment/unit format (Apt 123, Unit 5B, Suite 200)
            
            ## Credit Card Guidelines  
            Generate realistic but non-functional card data:
            - **num**: 16 digits formatted as XXXX-XXXX-XXXX-XXXX
            - **cvv**: 3 digits (000-999)
            - **exp_date**: MM/YYYY format, future dates only
            
            ## Biography Creation ("about_me")
            Create engaging, realistic biographies that include:
            
            ### Personality Elements
            - 1-3 personality traits (curious, adventurous, analytical, etc.)
            - Authentic voice and writing style
            - Cultural and demographic appropriateness
            
            ### Interests & Hobbies  
            - 2-4 specific hobbies or activities
            - 1-3 broader interests or passion areas
            - 1-2 life goals or aspirations
            
            ### Biography Templates
            Use varied narrative structures:
            - "I'm a [trait] person who loves [hobbies]..."
            - "When I'm not working, you can find me [activity]..."  
            - "Life is all about balance for me. I enjoy [interests]..."
            - "As someone who's [trait], I find great joy in [hobby]..."
            
            ## Data Validation Reminders
            - Email uniqueness is enforced (check existing users)
            - Phone numbers should follow consistent formatting
            - Date formats must be exact (YYYY-MM-DD)
            - Credit card expiration dates must be in the future
            - Salary values should be realistic for the demographic
            
            ## Cultural Sensitivity
            - Match names to appropriate cultural backgrounds
            - Consider regional variations in address formats
            - Use realistic company names for the user's location
            - Ensure hobbies and interests are culturally appropriate
            
            When creating profiles, aim for diversity in:
            - Geographic representation
            - Age distribution  
            - Interest variety
            - Socioeconomic backgrounds
            - Cultural backgrounds
            """;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/sse/**") // Ścieżka Twoich endpointów MCP
                        .allowedOrigins("*")   // W produkcji zamień na konkretny adres Inspectora
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
                registry.addMapping("/mcp/**") // Ścieżka Twoich endpointów MCP
                        .allowedOrigins("*")   // W produkcji zamień na konkretny adres Inspectora
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public ToolCallbackProvider myTools(UserService userService) {
        ToolCallback[] tools = ToolCallbacks.from(new UserTools(userService));
        return ToolCallbackProvider.from(tools);
    }

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> myResources() {
        String uri = "resource://flow-diagram";
        var flowDiagramResource = McpSchema.Resource.builder()
                .uri(uri)
                .name("Flow Diagram")
                .mimeType("image/png")
                .build();
        var resourceSpecification = new McpServerFeatures.SyncResourceSpecification(flowDiagramResource, (exchange, request) -> {
            try {
                var imageResource = new ClassPathResource("flow.png");
                byte[] imageBytes = imageResource.getContentAsByteArray();

                String base64Data = Base64.getEncoder().encodeToString(imageBytes);

                var content = new McpSchema.BlobResourceContents(uri, "image/png", base64Data);

                return new McpSchema.ReadResourceResult(List.of(content));
            } catch (Exception e) {
                throw new RuntimeException("Failed to read image resource", e);
            }
        });

        return List.of(resourceSpecification);
    }

    @Bean
    public List<McpServerFeatures.SyncPromptSpecification> myPrompts() {
        var promptSearchStrategy = new McpSchema.Prompt("search-strategy-helper", "Helps formulate effective user search queries.", Collections.emptyList());
        var promptProfileCreation = new McpSchema.Prompt("profile-creation-guide", "Guides the creation of realistic user profiles.", Collections.emptyList());

        var promptSearchStrategySpecification = new McpServerFeatures.SyncPromptSpecification(promptSearchStrategy, (exchange, getPromptRequest) -> {
            var userMessage = new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent(SEARCH_ASSIST_PROMPT));
            return new McpSchema.GetPromptResult("Search strategy message", List.of(userMessage));
        });

        var promptProfileCreationSpecification = new McpServerFeatures.SyncPromptSpecification(promptProfileCreation, (exchange, getPromptRequest) -> {
            var userMessage = new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent(SEARCH_ASSIST_PROMPT));
            return new McpSchema.GetPromptResult("Profile create message", List.of(userMessage));
        });

        return List.of(promptSearchStrategySpecification, promptProfileCreationSpecification);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserService userService(RestTemplate restTemplate) {
        return new UserService(restTemplate);
    }
}
