package com.app.quizzservice.request.payload;

import java.util.List;

public record AddTest(
        String examName,
        boolean hasMonitor,
        int numberOfQuestion,
        String startDate,
        String endDate,
        List<AddQuestion> listQuestion,
        int subjectId,
        int time
) {
}
