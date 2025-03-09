package com.app.quizzservice.request.response;

import java.util.List;

public record CourseResponse(
        Long courseId,
        String courseCode,
        List<SubjectCourseRepsonse> subjects
) {
}
