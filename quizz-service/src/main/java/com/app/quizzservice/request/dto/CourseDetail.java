package com.app.quizzservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CourseDetail {
    Long courseId;
    String courseCode;

    Long testId;
    String name;
    String startDate;
    String endDate;

    Long subjectId;
    String subjectName;
    String subjectIcon;

    Integer duration;
    Boolean isAttempted;
}
