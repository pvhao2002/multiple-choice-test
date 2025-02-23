package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.ReviewDTO;

import java.util.List;

public record ReviewResponse(
        long testId,
        boolean hasMonitor,
        String name,
        long testAttemptId,
        int numberOfWarning,
        int totalCorrect,
        int score,
        String startTime,
        String endTime,
        int totalQuestions,
        List<ReviewQuestionResponse> questions
) {
    public ReviewResponse(List<ReviewDTO> dtos) {
        this(
                dtos.getFirst().testId(),
                dtos.getFirst().hasMonitor(),
                dtos.getFirst().name(),
                dtos.getFirst().testAttemptId(),
                dtos.getFirst().numberOfWarning(),
                dtos.getFirst().totalCorrect(),
                dtos.getFirst().score(),
                dtos.getFirst().startTime(),
                dtos.getFirst().endTime(),
                dtos.getFirst().totalQuestions(),
                dtos.stream().map(ReviewQuestionResponse::new).toList()
        );
    }

    public ReviewResponse() {
        this(0, false, "", 0, 0, 0, 0, "", "", 0, List.of());
    }
}
