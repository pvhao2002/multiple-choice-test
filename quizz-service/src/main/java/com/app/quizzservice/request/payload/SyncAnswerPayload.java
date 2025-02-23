package com.app.quizzservice.request.payload;

import java.util.List;

public record SyncAnswerPayload(
        long testAttemptId,
        int numberWarning,
        long examId,
        List<AnswerPayload> answers
) {
}
