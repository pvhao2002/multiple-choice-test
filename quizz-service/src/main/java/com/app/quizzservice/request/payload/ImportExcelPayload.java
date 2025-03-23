package com.app.quizzservice.request.payload;

public record ImportExcelPayload(
        String name,
        boolean hasMonitor,
        String startDate,
        String endDate,
        long subjectId,
        int time
) {
}
