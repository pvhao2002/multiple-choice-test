package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Course {
    long courseId;
    String courseCode;
    int total;
    int totalTest;

    @Builder.Default
    String status = StatusEnum.ACTIVE.name();
}
