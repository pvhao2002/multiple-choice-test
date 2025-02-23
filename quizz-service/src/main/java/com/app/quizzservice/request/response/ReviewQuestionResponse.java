package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.ReviewDTO;

public record ReviewQuestionResponse(
        long questionId,
        String userAnswer,
        boolean isCorrect,
        int no,
        String image,
        String content,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctAnswer
) {
    public ReviewQuestionResponse(ReviewDTO dto) {
        this(
                dto.questionId(),
                dto.userAnswer(),
                dto.isCorrect(),
                dto.no(),
                dto.image(),
                dto.content(),
                dto.optionA(),
                dto.optionB(),
                dto.optionC(),
                dto.optionD(),
                dto.correctAnswer()
        );
    }
}
