package com.app.quizzservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Long testId;
    private String testName;
    private String email;
    private String testDate;
    private Integer totalQuestions;
    private Integer totalCorrect;
    private Integer totalWarning;
    private Boolean hasMonitor;
    private String lastUpdate;
}
