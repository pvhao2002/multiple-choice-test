package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.StatusEnum;
import com.app.quizzservice.utils.JDBCUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Test {
    long testId;
    long subjectId;
    String name;
    String startDate;
    String endDate;
    boolean close;
    int totalQuestions;
    StatusEnum status;
    boolean hasMonitor;

    public Test(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_id"),
                rs.getLong("subject_id"),
                rs.getString("name"),
                rs.getString("start_date"),
                rs.getString("end_date"),
                JDBCUtils.getValueResultSet(rs, Boolean.class, false, "close"),
                rs.getInt("total_questions"),
                StatusEnum.valueOf(rs.getString("status").toUpperCase()),
                rs.getBoolean("has_monitor")
        );
    }
}
