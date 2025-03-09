package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.CourseDetail;

public record ExamCourseResponse(
        Long testId,
        String name,
        String startDate,
        String endDate
) {
    public ExamCourseResponse(CourseDetail detail) {
        this(
                detail.getTestId(),
                detail.getName(),
                detail.getStartDate(),
                detail.getEndDate()
        );
    }
}
