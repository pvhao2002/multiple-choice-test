package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.ExamDetailDTO;

import java.util.Optional;

public record QuestionResponse(
        long questionId,
        int no,
        String image,
        String content,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String userAnswer
) {
    public QuestionResponse(ExamDetailDTO dto) {
        this(
                dto.questionId(),
                dto.no(),
                dto.image(),
                dto.content(),
                dto.optionA(),
                dto.optionB(),
                dto.optionC(),
                dto.optionD(),
                dto.answer()
        );
    }
}
