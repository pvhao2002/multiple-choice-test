package com.app.quizzservice.request.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ExamDetailDTO(
        long testId,
        String name,
        int totalQuestions,
        boolean hasMonitor,
        long questionId,
        int no,
        String image,
        String content,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String answer,
        String status,
        long testAttemptId,
        int numberTreating
) {
    public ExamDetailDTO(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_id"),
                rs.getString("name"),
                rs.getInt("total_questions"),
                rs.getBoolean("has_monitor"),
                rs.getLong("question_id"),
                rs.getInt("no"),
                rs.getString("image"),
                rs.getString("content"),
                rs.getString("option_a"),
                rs.getString("option_b"),
                rs.getString("option_c"),
                rs.getString("option_d"),
                rs.getString("answer"),
                rs.getString("status"),
                rs.getLong("test_attempt_id"),
                rs.getInt("number_treating")
        );
    }
}
