package com.app.quizzservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Data
@AllArgsConstructor
public class ExamDTO {
    private long testId;
    private String name;
    private boolean hasMonitor;
    private int totalQuestion;
    private int count;
    private int totalRow;
    private Date updatedAt;

    public ExamDTO(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_id"),
                rs.getString("name"),
                rs.getBoolean("has_monitor"),
                rs.getInt("total_questions"),
                rs.getInt("cnt"),
                rs.getInt("total_rows"),
                rs.getDate("last_update")
        );
    }
}
