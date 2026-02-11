package com.serkowski;

import com.serkowski.clients.DialClient;
import com.serkowski.clients.DialCustomClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.serkowski.clients.Constants.DEFAULT_SYSTEM_PROMPT;

@SpringBootApplication
public class Application {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public CommandLineRunner run(DialClient dialClient) {
//        return args -> {
//            System.out.println("Enter deployment_name (e.g. 'gpt-3.5-turbo'): ");
//            Scanner scanner = new Scanner(System.in);
//            String deploymentName = scanner.nextLine();
//            System.out.println("Enter system prompt (Enter = default): ");
//            String systemPrompt = scanner.nextLine();
//            List<Message> messages = new ArrayList<>();
//            if (systemPrompt.isBlank()) {
//                messages.add(SystemMessage.builder()
//                        .text(DEFAULT_SYSTEM_PROMPT)
//                        .build());
//            } else {
//                messages.add(SystemMessage.builder()
//                        .text(systemPrompt)
//                        .build());
//            }
//
//            while (true) {
//                System.out.println("You: ");
//                String userInput = scanner.nextLine();
//                if (userInput.equals("exit")) {
//                    break;
//                }
//                messages.add(UserMessage.builder()
//                        .text(userInput)
//                        .build());
////                AssistantMessage aiResponse = dialClient.getCompletions(messages, deploymentName);
//                System.out.println("AI (stream): ");
//                AssistantMessage aiResponse = dialClient.getCompletionsStream(messages, deploymentName);
//                System.out.println("");
////                System.out.println(aiResponse.getText());
//                messages.add(aiResponse);
//            }
//        };
//    }

    @Bean
    public CommandLineRunner run(DialCustomClient dialClient) {
        return args -> {
            System.out.println("Enter deployment_name (e.g. 'gpt-3.5-turbo'): ");
            Scanner scanner = new Scanner(System.in);
            String deploymentName = scanner.nextLine();
            System.out.println("Enter system prompt (Enter = default): ");
            String systemPrompt = scanner.nextLine();
            List<Message> messages = new ArrayList<>();
            if (systemPrompt.isBlank()) {
                messages.add(SystemMessage.builder()
                        .text(DEFAULT_SYSTEM_PROMPT)
                        .build());
            } else {
                messages.add(SystemMessage.builder()
                        .text(systemPrompt)
                        .build());
            }

            while (true) {
                System.out.println("You: ");
                String userInput = scanner.nextLine();
                if (userInput.equals("exit")) {
                    break;
                }
                messages.add(UserMessage.builder()
                        .text(userInput)
                        .build());
//                AssistantMessage aiResponse = dialClient.getCompletions(messages, deploymentName);
                System.out.println("AI (stream): ");
                AssistantMessage aiResponse = dialClient.getCompletionsStream(messages, deploymentName);
                System.out.println("");
//                System.out.println(aiResponse.getText());
                messages.add(aiResponse);
            }
        };
    }
}
