package com.namak.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namak.models.Question;

import reactor.core.publisher.Flux;

@Service
public class GenAIService {
  private final VertexAiGeminiChatModel chatModel;
  private final DocumentService documentService;
  private final ObjectMapper objectMapper;

  public GenAIService(VertexAiGeminiChatModel chatModel, DocumentService documentService,
      ObjectMapper objectMapper) {
    this.chatModel = chatModel;
    this.documentService = documentService;
    this.objectMapper = objectMapper;

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
        """
        .formatted(numQuestions, topic);
    return chatModel.stream(prompt);
  }

  public Flux<Question> generateQuestionsFromDocument(String documentName) {
    String documentContent = documentService.readDocxFile(documentName);
    String prompt = """
        You are an automated quiz generation service. Your sole function is to generate a JSON array of questions based on the provided document.

        **Strict Output Rules:**
        1. The entire response MUST be a single, valid JSON array.
        2. Do NOT output any text, explanation, or markdown code fences before or after the JSON array.
        3. The response must start with `[` and end with `]`.

        **JSON Schema:**
        Each object in the array must contain these exact fields:
        - `question`: A string containing the question text.
        - `answer`: A string containing the correct answer.
        - `sop`: A string identifying the Standard Operating Procedure (SOP) from which the question is derived.
        - `lob`: A string identifying the Line of Business (LOB) the question is related to.
        - `answerDescription`: A string explaining why the selected answer is correct.
        - `options`: An array of 4 strings representing the answer choices.

        **Example of a single question object:**
        ```json
        {
          "question": "What is the primary purpose of the Gemini API?",
          "answer": "To provide programmatic access to Google's large language models.",
          "sop": "API Usage Guidelines",
          "lob": "Technology",
          "answerDescription": "The Gemini API is designed to give developers access to Google's powerful large language models, allowing them to build AI-powered features and applications.",
          "options": [
            "To provide programmatic access to Google's large language models.",
            "To manage Google Cloud server instances.",
            "To analyze user data for marketing purposes.",
            "To create virtual reality environments."
          ]
        }
        ```
        Now, generate as many high-quality questions as possible based on the following document. Minimum of 75 questions.
        """
        + documentContent;

    return chatModel.stream(prompt)
        .collectList()
        .map(list -> String.join("", list))
        .flatMapMany(jsonString -> {
          try {
            FileUtils.writeStringToFile(new File("test.json"), jsonString, Charset.forName("UTF-8"));
            return Flux.fromIterable(objectMapper.readValue(jsonString, new TypeReference<List<Question>>() {
            }));
          } catch (JsonProcessingException e) {
            return Flux.error(new RuntimeException("Failed to parse JSON from AI model: " + jsonString, e));
          } catch (IOException e) {
            return Flux.error(new RuntimeException("Failed to write or process AI response: " + e.getMessage(), e));
          }
        });
  }
}
