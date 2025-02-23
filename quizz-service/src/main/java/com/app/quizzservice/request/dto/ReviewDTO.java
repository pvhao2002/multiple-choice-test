package com.app.quizzservice.request.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ReviewDTO(
        long testId,
        boolean hasMonitor,
        String name,
        long testAttemptId,
        int numberOfWarning,
        int totalCorrect,
        int score,
        String startTime,
        String endTime,
        int totalQuestions,
        long questionId,
        String userAnswer,
        boolean isCorrect,
        int no,
        String image,
        String content,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctAnswer
) {
    public ReviewDTO(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_id"),
                rs.getBoolean("has_monitor"),
                rs.getString("name"),
                rs.getLong("test_attempt_id"),
                rs.getInt("number_of_warning"),
                rs.getInt("total_correct"),
                rs.getInt("score"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getInt("total_questions"),
                rs.getLong("question_id"),
                rs.getString("user_answer"),
                rs.getBoolean("is_correct"),
                rs.getInt("no"),
                rs.getString("image"),
                rs.getString("content"),
                rs.getString("option_a"),
                rs.getString("option_b"),
                rs.getString("option_c"),
                rs.getString("option_d"),
                rs.getString("correct_answer")
        );
    }
}
