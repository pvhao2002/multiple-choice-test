package com.app.quizzservice.request.payload;

import com.app.quizzservice.model.AskQuestion;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeminiPayload {

    @Builder.Default
    List<Content> contents = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        String role;
        @Builder.Default
        List<PartContent> parts = new ArrayList<>();

        public static List<Content> fromHistory(AskQuestion history) {
            return List.of(
                    Content.builder()
                           .role("user")
                           .parts(List.of(new PartContent(history.getQuestion())))
                           .build(),
                    Content.builder()
                           .role("model")
                           .parts(List.of(new PartContent(history.getAnswer())))
                           .build()
            );
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartContent {
        String text;
    }
}
