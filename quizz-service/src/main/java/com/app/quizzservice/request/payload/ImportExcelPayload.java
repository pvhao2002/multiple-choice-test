package com.app.quizzservice.request.payload;

public record ImportExcelPayload(
        String name,
        boolean hasMonitor,
        long subjectId
) {
}
