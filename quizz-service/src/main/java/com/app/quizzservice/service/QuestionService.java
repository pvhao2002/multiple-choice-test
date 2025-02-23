package com.app.quizzservice.service;

import com.app.quizzservice.model.Question;
import com.app.quizzservice.request.payload.AddQuestion;
import com.app.quizzservice.request.payload.ImportExcelQuetion;
import com.app.quizzservice.utils.Constants;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private final NamedParameterJdbcTemplate writeDb;

    public QuestionService(NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    public List<Question> findByTest(Long testId) {
        if (testId == null) {
            return List.of();
        }
        return writeDb.query(
                "SELECT * FROM questions WHERE test_id = :testId and is_deleted = 0",
                new MapSqlParameterSource("testId", testId),
                (rs, rowNum) -> new Question(rs)
        );
    }

    @Transactional
    public void save(AddQuestion request, String imageUrl) {
        var sql = "CALL up_UpdateQuestion(:image, :content, :optionA, :optionB, :optionC, :optionD, :answer, :questionId)";
        writeDb.update(sql, request.toMap(imageUrl, true));
    }

    @Transactional
    public void insert(AddQuestion request, String imageUrl) {
        var sql = "CALL up_InsertQuestion(:image, :content, :optionA, :optionB, :optionC, :optionD, :answer, :examId)";
        writeDb.update(sql, request.toMap(imageUrl, false));
    }

    @Transactional
    public String delete(Long qid) {
        writeDb.update("CALL up_RemoveQuestion(:qid);", new MapSqlParameterSource("qid", qid));
        return "Question deleted successfully";
    }

    @Transactional
    public String save(ArrayList<ImportExcelQuetion> lists) {
        var params = lists.stream()
                          .map(ImportExcelQuetion::toMap)
                          .toArray(MapSqlParameterSource[]::new);
        writeDb.batchUpdate(
                "CALL up_InsertQuestion('', :content, :optionA, :optionB, :optionC, :optionD, :answer, :examId)",
                params
        );
        return Constants.SUCCESS;
    }

}
