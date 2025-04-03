package com.app.quizzservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AboutDTO {
    private List<SubjectAboutDTO> subjects;
    private List<CourseAboutDTO> courses;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseAboutDTO {
        private String courseCode;
        private String startDate;

        public CourseAboutDTO(ResultSet rs) throws SQLException {
            this(
                    rs.getString("course_code"),
                    rs.getString("start_date")
            );
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectAboutDTO {
        private String subjectName;

        public SubjectAboutDTO(ResultSet rs) throws SQLException {
            this(
                    rs.getString("subject_name")
            );
        }
    }
}
