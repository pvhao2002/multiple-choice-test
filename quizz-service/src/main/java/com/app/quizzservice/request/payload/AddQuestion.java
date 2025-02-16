package com.app.quizzservice.request.payload;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public record AddQuestion(
        long questionId,
        long examId,
        String image,
        String content,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String answer
) {
    public MapSqlParameterSource toMap(long examId, int no) {
        return new MapSqlParameterSource()
                .addValue("no", no)
                .addValue("image", image())
                .addValue("content", content())
                .addValue("optionA", optionA())
                .addValue("optionB", optionB())
                .addValue("optionC", optionC())
                .addValue("optionD", optionD())
                .addValue("answer", answer())
                .addValue("examId", examId);
    }

    public MapSqlParameterSource toMap(String imageUrl, boolean update) {
        var map = new MapSqlParameterSource()
                .addValue("image", imageUrl)
                .addValue("content", content())
                .addValue("optionA", optionA())
                .addValue("optionB", optionB())
                .addValue("optionC", optionC())
                .addValue("optionD", optionD())
                .addValue("answer", answer());
        if (update) {
            map.addValue("questionId", questionId());
        } else {
            map.addValue("examId", examId());
        }
        return map;
    }
}
