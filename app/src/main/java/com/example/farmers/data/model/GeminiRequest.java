package com.example.farmers.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeminiRequest {

    @SerializedName("system_instruction")
    public SystemInstruction systemInstruction;

    @SerializedName("contents")
    public List<Content> contents;

    public GeminiRequest(String systemPrompt, List<Content> contents) {
        this.systemInstruction = new SystemInstruction(systemPrompt);
        this.contents = contents;
    }

    public static class SystemInstruction {
        @SerializedName("parts")
        public List<Part> parts;

        public SystemInstruction(String text) {
            this.parts = List.of(new Part(text));
        }
    }

    public static class Content {
        @SerializedName("role")
        public String role;
        @SerializedName("parts")
        public List<Part> parts;

        public Content(String role, String text) {
            this.role = role;
            this.parts = List.of(new Part(text));
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
