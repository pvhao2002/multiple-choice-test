package com.app.quizzservice.request.payload;

public record UpdateTestRequest(
        long testId,
        long subjectId,
        String name,
        boolean hasMonitor
) {
}
