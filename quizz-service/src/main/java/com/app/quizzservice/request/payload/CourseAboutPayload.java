package com.app.quizzservice.request.payload;

import java.sql.ResultSet;
import java.sql.SQLException;

public record CourseAboutPayload(
        Long aboutId,
        String content
) {
    public CourseAboutPayload(ResultSet rs) throws SQLException {
        this(
                rs.getLong("aboutId"),
                rs.getString("content")
        );
    }
}
