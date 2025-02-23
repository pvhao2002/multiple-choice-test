package com.app.quizzservice.request.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ResultDTO(
        long testId,
        long testAttemptId,
        String name,
        boolean hasMonitor,
        int score,
        int totalCorrect,
        String status,
        int totalQuestions,
        int numberWarning,
        int totalRows,
        String startTime,
        String finishTime
) {
    public ResultDTO(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_id"),
                rs.getLong("test_attempt_id"),
                rs.getString("name"),
                rs.getBoolean("has_monitor"),
                rs.getInt("score"),
                rs.getInt("total_correct"),
                rs.getString("status_str"),
                rs.getInt("total_questions"),
                rs.getInt("number_of_warning"),
                rs.getInt("total_rows"),
                rs.getString("start_time"),
                rs.getString("finish_time")
        );
    }
}
