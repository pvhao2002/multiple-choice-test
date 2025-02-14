package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.AnswerEnum;
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
public class TestAttemptDetail {
    long testAttemptDetailId;
    long testAttemptId;
    long questionId;
    AnswerEnum answer;
    int points;
    boolean correct;

    public TestAttemptDetail(ResultSet rs) throws SQLException {
        this(
                rs.getLong("test_attempt_detail_id"),
                rs.getLong("test_attempt_id"),
                rs.getLong("question_id"),
                AnswerEnum.valueOf(rs.getString("answer")),
                rs.getInt("points"),
                rs.getBoolean("correct")
        );
    }
}
