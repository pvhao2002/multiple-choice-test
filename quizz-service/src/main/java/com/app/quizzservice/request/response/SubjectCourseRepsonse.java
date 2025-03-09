package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.CourseDetail;

import java.util.List;
import java.util.Map;

public record SubjectCourseRepsonse(
        Long subjectId,
        String subjectName,
        String subjectIcon,
        List<ExamCourseResponse> exams
) {
    public SubjectCourseRepsonse(Map.Entry<Long, List<CourseDetail>> entry) {
        this(
                entry.getKey(),
                entry.getValue().getFirst().getSubjectName(),
                entry.getValue().getFirst().getSubjectIcon(),
                entry.getValue().stream().map(ExamCourseResponse::new).toList()
        );
    }
}
