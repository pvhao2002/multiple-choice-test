package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.TestStatusEnum;
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
public class TestAttempt {
    long testAttemptId;
    long testId;
    long userId;
    int score;
    int totalQuestions;
    int totalCorrect;
    TestStatusEnum statusStr;
    boolean status;
    String description;
    int numberOfWarnings;

    public TestAttempt(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_attempt_id"),
                rs.getLong("test_id"),
                rs.getLong("user_id"),
                rs.getInt("score"),
                rs.getInt("total_questions"),
                rs.getInt("total_correct"),
                TestStatusEnum.valueOf(rs.getString("status_str")),
                rs.getBoolean("status"),
                rs.getString("description"),
                rs.getInt("number_of_warnings")
        );
    }
}
