package com.namak.namak.services;

import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class ChatService {
    private final VertexAiGeminiChatModel chatModel;

    public ChatService(VertexAiGeminiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public Flux<String> chat(String prompt) {
        return chatModel.stream(prompt);
    }

    public Flux<String> generateQuiz(String topic, int numQuestions) {
        String prompt = """
                "Generate a quiz with %s questions on the topic of %s. The output must be a single, valid JSON array.
                Each element in the array should be a JSON object representing a question.
                Each question object must have a 'question' field (string),
                an 'options' field (an array of objects with 'option' and 'description' strings), and an 'answer' field (string)."
                """.formatted(numQuestions, topic);
        return chatModel.stream(prompt);
    }
}
