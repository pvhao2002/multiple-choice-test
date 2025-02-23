package com.app.quizzservice.request.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public record UserTestWeekDTO(
        String attemptDate,
        int totalAttempts,
        String statusStr
) {
    public UserTestWeekDTO(ResultSet rs) throws SQLException {
        this(
                rs.getString("attempt_date"),
                rs.getInt("total_attempts"),
                rs.getString("status_str")
        );
    }
}
