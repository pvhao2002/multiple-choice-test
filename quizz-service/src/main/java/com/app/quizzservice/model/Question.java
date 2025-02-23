package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.AnswerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Question {
    long questionId;
    long testId;
    int no;
    String image;
    boolean hasImage;
    String content;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    AnswerEnum answer;
    int point;

    public Question(ResultSet rs) throws SQLException {
        this(
                rs.getLong("question_id"),
                rs.getLong("test_id"),
                rs.getInt("no"),
                rs.getString("image"),
                rs.getBoolean("has_image"),
                rs.getString("content"),
                rs.getString("option_a"),
                rs.getString("option_b"),
                rs.getString("option_c"),
                rs.getString("option_d"),
                AnswerEnum.valueOf(rs.getString("answer").toUpperCase()),
                rs.getInt("points")
        );
    }

    public MapSqlParameterSource toMap(int num, long examId) {
        return new MapSqlParameterSource()
                .addValue("no", num)
                .addValue("image", getImage())
                .addValue("content", getContent())
                .addValue("optionA", getOptionA())
                .addValue("optionB", getOptionB())
                .addValue("optionC", getOptionC())
                .addValue("optionD", getOptionD())
                .addValue("answer", getAnswer().name())
                .addValue("examId", examId);
    }
}
