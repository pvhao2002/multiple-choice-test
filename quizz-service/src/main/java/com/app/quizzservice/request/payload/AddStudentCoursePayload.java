package com.app.quizzservice.request.payload;

import java.util.List;

public record AddStudentCoursePayload(
        long courseId,
        List<String> studentIds
) {
}
