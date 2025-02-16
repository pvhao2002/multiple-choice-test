package com.app.quizzservice.service;

import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.Test;
import com.app.quizzservice.request.dto.ExamDTO;
import com.app.quizzservice.request.payload.AddTest;
import com.app.quizzservice.request.payload.UpdateTestRequest;
import com.app.quizzservice.request.response.ExamResponse;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.PagingUtil;
import lombok.extern.java.Log;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Log
@Service
public class ExamService {
    private final NamedParameterJdbcTemplate writeDb;

    public ExamService(@WriteDB NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    @Transactional
    public String save(AddTest data) {
        var paramExam = new MapSqlParameterSource()
                .addValue("examName", data.examName())
                .addValue("hasMonitor", data.hasMonitor())
                .addValue("numberOfQuestion", data.numberOfQuestion())
                .addValue("subjectId", data.subjectId());
        var examId = writeDb.queryForObject(
                "CALL up_SaveExam(:examName, :hasMonitor, :numberOfQuestion, :subjectId)",
                paramExam,
                Long.class
        );
        var index = new AtomicInteger(1);
        var paramQuestion = data.listQuestion()
                                .stream()
                                .map(q -> q.toMap(examId, index.getAndIncrement()))
                                .toList()
                                .toArray(new MapSqlParameterSource[0]);
        writeDb.batchUpdate(
                "CALL up_SaveQuestion(:no, :image, :content, :optionA, :optionB, :optionC, :optionD, :answer, :examId)",
                paramQuestion
        );
        return Constants.SUCCESS;
    }

    @Transactional
    public String save(UpdateTestRequest request) {
        var param = new MapSqlParameterSource()
                .addValue("eid", request.testId())
                .addValue("name", request.name())
                .addValue("hasMonitor", request.hasMonitor())
                .addValue("subjectId", request.subjectId());
        writeDb.update(
                """
                        update test
                        set subject_id = IFNULL(:subjectId, subject_id),
                           name = IFNULL(:name, name),
                           has_monitor = IFNULL(:hasMonitor, has_monitor)
                        where test_id = :eid;
                        """,
                param
        );
        return Constants.SUCCESS;
    }

    public String delete(Long eid) {
        var param = new MapSqlParameterSource().addValue("eid", eid);
        writeDb.update("update test set status = 'inactive' where test_id = :eid", param);
        return Constants.SUCCESS;
    }

    public PagingContainer<Test> findAll(Integer page, Integer size, String key) {
        var sql = """
                SELECT *
                FROM test
                WHERE name LIKE :key
                AND status = 'active'
                ORDER BY created_at DESC
                LIMIT :size OFFSET :offset
                """;
        var param = new MapSqlParameterSource()
                .addValue("key", "%" + key + "%")
                .addValue("size", size)
                .addValue("offset", PagingUtil.calculateOffset(page, size));
        var list = writeDb.query(sql, param, (rs, i) -> new Test(rs));
        var total = writeDb.queryForObject(
                "SELECT COUNT(1) FROM test WHERE name LIKE :key AND status = 'active'",
                param,
                Integer.class
        );
        return new PagingContainer<>(page, size, total, list);
    }

    public PagingContainer<ExamResponse> findAll(Integer page, Integer size, Long sid, Integer mode) {
        var offset = PagingUtil.calculateOffset(page, size);
        var sql = "CALL up_GetListExam(:offset, :size, :sid, :mode)";
        var param = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("offset", offset)
                .addValue("sid", sid)
                .addValue("mode", mode);
        var list = writeDb.query(sql, param, (rs, i) -> new ExamDTO(rs));
        var total = list.stream().findFirst().map(ExamDTO::getTotalRow).orElse(0);
        return new PagingContainer<>(page, size, total, list.stream().map(ExamResponse::new).toList());
    }
}
