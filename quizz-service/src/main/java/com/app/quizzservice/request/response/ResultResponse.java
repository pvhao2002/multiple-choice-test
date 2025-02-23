package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.ResultDTO;

public record ResultResponse(
        long testId,
        long testAttemptId,
        String name,
        boolean hasMonitor,
        int score,
        int totalCorrect,
        String status,
        int totalQuestions,
        int numberWarning,
        String startTime,
        String finishTime
) {
    public ResultResponse(ResultDTO dto) {
        this(
                dto.testId(),
                dto.testAttemptId(),
                dto.name(),
                dto.hasMonitor(),
                dto.score(),
                dto.totalCorrect(),
                dto.status(),
                dto.totalQuestions(),
                dto.numberWarning(),
                dto.startTime(),
                dto.finishTime()
        );
    }
}
