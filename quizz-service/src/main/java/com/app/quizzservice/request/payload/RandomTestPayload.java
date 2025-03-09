package com.app.quizzservice.request.payload;

import java.util.List;

public record RandomTestPayload(
        int numberQuestion,
        String name,
        String startDate,
        String endDate,
        boolean hasMonitor,
        long subjectId,
        List<Long> testIds
) {
}
