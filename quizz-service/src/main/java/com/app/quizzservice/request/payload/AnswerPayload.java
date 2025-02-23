package com.app.quizzservice.request.payload;

public record AnswerPayload(
        long qid,
        String answer
) {
}
