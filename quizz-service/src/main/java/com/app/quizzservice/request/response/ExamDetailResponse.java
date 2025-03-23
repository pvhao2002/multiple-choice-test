package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.ExamDetailDTO;
import lombok.AllArgsConstructor;

import java.util.List;

public record ExamDetailResponse(
        long testId,
        String name,
        int totalQuestions,
        boolean hasMonitor,
        boolean hasStarted,
        long testAttemptId,
        int numberTreating,
        List<QuestionResponse> questions,
        int time,
        int remainingTime
) {
    public ExamDetailResponse(List<ExamDetailDTO> dtos) {
        this(
                dtos.getFirst().testId(),
                dtos.getFirst().name(),
                dtos.getFirst().totalQuestions(),
                dtos.getFirst().hasMonitor(),
                dtos.getFirst().status().equals("incomplete"),
                dtos.getFirst().testAttemptId(),
                dtos.getFirst().numberTreating(),
                dtos.stream().map(QuestionResponse::new).toList(),
                dtos.getFirst().time(),
                dtos.getFirst().remainingTime()
        );
    }

    public ExamDetailResponse() {
        this(0, "", 0, false, false, 0, 0, List.of(), 0, 0);
    }
}
