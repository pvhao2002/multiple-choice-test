package com.app.quizzservice.service;

import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.AskQuestion;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {
    private final NamedParameterJdbcTemplate writeDb;

    public ChatbotService(@WriteDB NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    @Transactional
    public void saveHistory(String text, String response, String studentId) {
        var sql = "insert into asked_questions (student_id, question, answer) values (:studentId, :question, :answer);";
        var params = Map.of("studentId", studentId, "question", text, "answer", response);
        writeDb.update(sql, params);
    }

    public List<AskQuestion> findAll(String studentId) {
        var sql = "select * from asked_questions where student_id = :studentId;";
        var params = Map.of("studentId", studentId);
        return writeDb.query(sql, params, BeanPropertyRowMapper.newInstance(AskQuestion.class));
    }
}
