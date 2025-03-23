package com.app.quizzservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartByExam {
    private Long testId;
    private String name;
    private Integer courseCount;
}
