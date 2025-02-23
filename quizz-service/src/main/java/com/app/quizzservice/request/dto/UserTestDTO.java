package com.app.quizzservice.request.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public record UserTestDTO(
        String email,
        String fullName,
        String subject,
        String testName,
        boolean hasMonitor,
        int totalQuestion,
        int totalCorrect,
        String status,
        int numberOfWarning,
        String lastUpdate
) {
    public UserTestDTO(ResultSet rs) throws SQLException {
        this(
                rs.getString("email"),
                rs.getString("full_name"),
                rs.getString("subject"),
                rs.getString("test_name"),
                rs.getBoolean("has_monitor"),
                rs.getInt("total_questions"),
                rs.getInt("total_correct"),
                rs.getString("status_str"),
                rs.getInt("number_of_warning"),
                rs.getString("last_update")
        );
    }
}
