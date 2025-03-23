package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.CourseDetail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ExamCourseResponse(
        Long testId,
        String name,
        String startDate,
        String endDate,
        int time,
        String status,
        String cssClass,
        boolean isJoined
) {
    public ExamCourseResponse(CourseDetail detail) {
        this(
                detail.getTestId(),
                detail.getName(),
                detail.getStartDate(),
                detail.getEndDate(),
                detail.getDuration(),
                determineStatus(detail.getEndDate()),
                determineCssClass(detail.getEndDate()),
                detail.getIsAttempted()
        );

    }

    private static String determineStatus(String endDate) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var endDateTime = LocalDateTime.parse(endDate, formatter);
        return LocalDateTime.now().isBefore(endDateTime) ? "Open" : "Close";
    }

    private static String determineCssClass(String endDate) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var endDateTime = LocalDateTime.parse(endDate, formatter);
        return LocalDateTime.now().isBefore(endDateTime) ? "success" : "danger";
    }
}
