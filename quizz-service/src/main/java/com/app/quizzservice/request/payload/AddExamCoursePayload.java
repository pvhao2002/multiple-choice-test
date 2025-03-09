package com.app.quizzservice.request.payload;

import java.util.List;

public record AddExamCoursePayload(
        long courseId,
        List<Long> examIds
) {
}
