package com.app.quizzservice.request.response;

import java.sql.ResultSet;
import java.sql.SQLException;

public record DashboardAdminResponse(
        long userNumber,
        long subjectNumber,
        long examNumber
) {
    public DashboardAdminResponse(ResultSet rs) throws SQLException {
        this(
                rs.getInt("user"),
                rs.getInt("subject"),
                rs.getInt("exam")
        );
    }
}
