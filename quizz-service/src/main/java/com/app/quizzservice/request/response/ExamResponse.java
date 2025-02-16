package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.ExamDTO;

import java.util.Date;

public record ExamResponse(
        long testId,
        String name,
        boolean hasMonitor,
        int totalQuestions,
        int count,
        Date updatedAt
) {
    public ExamResponse(ExamDTO dto) {
        this(
                dto.getTestId(),
                dto.getName(),
                dto.isHasMonitor(),
                dto.getTotalQuestion(),
                dto.getCount(),
                dto.getUpdatedAt()
        );
    }
}
