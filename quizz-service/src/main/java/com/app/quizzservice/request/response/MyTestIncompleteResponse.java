package com.app.quizzservice.request.response;

import java.sql.ResultSet;
import java.sql.SQLException;

public record MyTestIncompleteResponse(
        String subject,
        String exam,
        boolean hasMonitor,
        int totalQuestion,
        int totalAnswered,
        int totalWarning,
        String lastUpdate
) {
    public MyTestIncompleteResponse(ResultSet rs) throws SQLException {
        this(
                rs.getString("subject"),
                rs.getString("exam"),
                rs.getBoolean("has_monitor"),
                rs.getInt("total_questions"),
                rs.getInt("total_answer"),
                rs.getInt("total_warning"),
                rs.getString("last_update")
        );
    }
}
