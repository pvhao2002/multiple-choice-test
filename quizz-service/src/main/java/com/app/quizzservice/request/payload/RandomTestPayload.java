package com.app.quizzservice.request.payload;

import java.util.List;

public record RandomTestPayload(
        int numberQuestion,
        String name,
        boolean hasMonitor,
        long subjectId,
        List<Long> testIds
) {
}
