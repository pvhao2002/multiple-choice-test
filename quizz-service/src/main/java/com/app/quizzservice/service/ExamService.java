package com.app.quizzservice.service;

import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.exception.AppException;
import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.Question;
import com.app.quizzservice.model.Test;
import com.app.quizzservice.model.enums.ErrorCodeEnum;
import com.app.quizzservice.request.dto.ExamDTO;
import com.app.quizzservice.request.dto.ExamDetailDTO;
import com.app.quizzservice.request.dto.ResultDTO;
import com.app.quizzservice.request.dto.ReviewDTO;
import com.app.quizzservice.request.payload.AddTest;
import com.app.quizzservice.request.payload.RandomTestPayload;
import com.app.quizzservice.request.payload.SyncAnswerPayload;
import com.app.quizzservice.request.payload.UpdateTestRequest;
import com.app.quizzservice.request.response.ExamDetailResponse;
import com.app.quizzservice.request.response.ExamResponse;
import com.app.quizzservice.request.response.ResultResponse;
import com.app.quizzservice.request.response.ReviewResponse;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.PagingUtil;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Log
@Service
public class ExamService {
    private final NamedParameterJdbcTemplate writeDb;
    private final JdbcTemplate writeDb2;

    public ExamService(@WriteDB NamedParameterJdbcTemplate writeDb, @WriteDB JdbcTemplate writeDb2) {
        this.writeDb = writeDb;
        this.writeDb2 = writeDb2;
    }

    @Transactional
    public String save(AddTest data) {
        var paramExam = new MapSqlParameterSource().addValue("examName", data.examName())
                                                   .addValue("hasMonitor", data.hasMonitor())
                                                   .addValue("numberOfQuestion", data.numberOfQuestion())
                                                   .addValue("startDate", data.startDate())
                                                   .addValue("endDate", data.endDate())
                                                   .addValue("subjectId", data.subjectId());
        var examId = writeDb.queryForObject(
                "CALL up_SaveExam(:examName, :hasMonitor, :numberOfQuestion, :subjectId, :startDate, :endDate)",
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
    public long save(String examName, boolean hasMonitor, long subjectId, String startDate, String endDate) {
        var paramExam = new MapSqlParameterSource()
                .addValue("examName", examName)
                .addValue("hasMonitor", hasMonitor)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate)
                .addValue("subjectId", subjectId);
        return writeDb.queryForObject(
                "CALL up_SaveExam(:examName, :hasMonitor, 0, :subjectId, :startDate, :endDate)",
                paramExam,
                Long.class
        );
    }

    @Transactional
    public String importExcel() {
        return Constants.SUCCESS;
    }

    @Transactional
    public String save(UpdateTestRequest request) {
        var param = new MapSqlParameterSource().addValue("eid", request.testId())
                                               .addValue("name", request.name())
                                               .addValue("hasMonitor", request.hasMonitor())
                                               .addValue("subjectId", request.subjectId());
        writeDb.update("""
                               update test
                               set subject_id = IFNULL(:subjectId, subject_id),
                                  name = IFNULL(:name, name),
                                  has_monitor = IFNULL(:hasMonitor, has_monitor)
                               where test_id = :eid;
                               """, param);
        return Constants.SUCCESS;
    }

    public String delete(Long eid) {
        var param = new MapSqlParameterSource().addValue("eid", eid);
        writeDb.update("update test set status = 'inactive' where test_id = :eid", param);
        return Constants.SUCCESS;
    }

    public PagingContainer<Test> findAll(Integer page, Integer size, String key) {
        var sql = """
                SELECT *,
                 end_date < NOW() as close
                FROM test
                WHERE name LIKE :key
                AND status = 'active'
                ORDER BY created_at DESC
                LIMIT :size OFFSET :offset
                """;
        var param = new MapSqlParameterSource().addValue("key", "%" + key + "%")
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
        var param = new MapSqlParameterSource().addValue("size", size)
                                               .addValue("offset", offset)
                                               .addValue("sid", sid)
                                               .addValue("mode", mode);
        var list = writeDb.query(sql, param, (rs, i) -> new ExamDTO(rs));
        var total = list.stream().findFirst().map(ExamDTO::getTotalRow).orElse(0);
        return new PagingContainer<>(page, size, total, list.stream().map(ExamResponse::new).toList());
    }

    public ExamDetailResponse detail(Long id, Long uid) {
        var sql = "CALL up_GetExamDetail(:userId, :examId)";
        var param = new MapSqlParameterSource().addValue("userId", uid).addValue("examId", id);
        var result = writeDb.query(sql, param, (rs, i) -> new ExamDetailDTO(rs));
        return CollectionUtils.isEmpty(result) ? new ExamDetailResponse() : new ExamDetailResponse(result);
    }

    public Long startTest(Long eid, Long uid) {
        var param = new MapSqlParameterSource().addValue("eid", eid).addValue("uid", uid);
        return writeDb.query("CALL up_StartTest(:eid, :uid)", param, (rs, i) -> rs.getLong("test_attempt_id"))
                      .stream()
                      .findFirst()
                      .orElse(0L);
    }

    @Transactional
    public void syncAnswer(SyncAnswerPayload payload) {
        var params = payload.answers()
                            .stream()
                            .filter(a -> a.qid() != 0 && StringUtils.isNotBlank(a.answer()))
                            .map(a -> new MapSqlParameterSource()
                                    .addValue("testAttemptId", payload.testAttemptId())
                                    .addValue("questionId", a.qid())
                                    .addValue("answer", a.answer())
                            )
                            .toList()
                            .toArray(new MapSqlParameterSource[0]);
        writeDb.batchUpdate(
                "CALL up_SyncAnswer(:testAttemptId, :questionId, :answer)",
                params
        );

        var sql = """
                    update test_attempts
                    set number_of_warning = IF(:number_of_warning < 1, number_of_warning, :number_of_warning)
                    where test_attempt_id = :test_attempt_id;
                """;
        var param = new MapSqlParameterSource().addValue("test_attempt_id", payload.testAttemptId())
                                               .addValue("number_of_warning", payload.numberWarning());
        writeDb.update(sql, param);
    }

    @Transactional
    public String submitTest(SyncAnswerPayload payload) {
        var params = payload.answers()
                            .stream()
                            .map(a -> new MapSqlParameterSource()
                                    .addValue("testAttemptId", payload.testAttemptId())
                                    .addValue("questionId", a.qid())
                                    .addValue("answer", a.answer())
                            )
                            .toList()
                            .toArray(new MapSqlParameterSource[0]);
        writeDb.batchUpdate(
                "CALL up_SyncAnswer(:testAttemptId, :questionId, :answer)",
                params
        );
        var sql = "CALL up_SubmitTest(:testAttemptId, :numberWarning)";
        var param = new MapSqlParameterSource()
                .addValue("testAttemptId", payload.testAttemptId())
                .addValue("numberWarning", payload.numberWarning());
        writeDb.update(sql, param);
        return Constants.SUCCESS;
    }

    public Object result(Integer page1, Integer page2, String key, Long uid) {
        var size = PagingUtil.getSizeOrDefault();
        var offset1 = PagingUtil.calculateOffset(page1, size);
        var offset2 = PagingUtil.calculateOffset(page2, size);
        var sql = "CALL up_GetListResult(:offset, :size, :key, :uid, :status)";
        var param = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("key", key)
                .addValue("uid", uid);
        var param1 = new MapSqlParameterSource(param.getValues())
                .addValue("status", "complete")
                .addValue("offset", offset1);
        var param2 = new MapSqlParameterSource(param.getValues())
                .addValue("status", "incomplete")
                .addValue("offset", offset2);

        var list1 = writeDb.query(sql, param1, (rs, i) -> new ResultDTO(rs));
        var list2 = writeDb.query(sql, param2, (rs, i) -> new ResultDTO(rs));
        var total1 = list1.stream().findFirst().map(ResultDTO::totalRows).orElse(0);
        var total2 = list2.stream().findFirst().map(ResultDTO::totalRows).orElse(0);
        var result1 = new PagingContainer<>(page1, size, total1, list1.stream().map(ResultResponse::new).toList());
        var result2 = new PagingContainer<>(page2, size, total2, list2.stream().map(ResultResponse::new).toList());
        return Map.of("complete", result1, "incomplete", result2);
    }

    public ReviewResponse review(long tid) {
        var sql = "CALL up_GetReview(:tid)";
        var param = new MapSqlParameterSource().addValue("tid", tid);
        var result = writeDb.query(sql, param, (rs, i) -> new ReviewDTO(rs));
        return CollectionUtils.isEmpty(result) ? new ReviewResponse() : new ReviewResponse(result);
    }

    @Transactional
    public void correctDataExam(long examId) {
        var param = new MapSqlParameterSource().addValue("id", examId);
        var sql = """
                update test
                set total_questions = (select count(1)
                                       from questions
                                       where test_id = :id)
                where test_id = :id;
                """;
        writeDb.update(sql, param);
    }

    @Transactional
    public Object random(RandomTestPayload payload) {
        writeDb2.execute("CREATE TEMPORARY TABLE IF NOT EXISTS test_ids (id INT NOT NULL)");
        var param1 = payload.testIds()
                            .stream()
                            .map(id -> new MapSqlParameterSource().addValue("id", id))
                            .toList()
                            .toArray(new MapSqlParameterSource[0]);
        writeDb.batchUpdate("INSERT INTO test_ids (id) VALUES (:id)", param1);

        var sqlGetRandomQuestion = """
                SELECT *
                FROM questions
                WHERE test_id IN (SELECT id FROM test_ids)
                LIMIT 1000
                """;
        var listQuestion = writeDb.query(sqlGetRandomQuestion, (rs, i) -> new Question(rs));
        writeDb2.execute("DROP TABLE IF EXISTS test_ids;");
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(listQuestion)) {
            throw new AppException(ErrorCodeEnum.NO_QUESTION);
        }

        var maxQuestion = Math.signum(listQuestion.size() - payload.numberQuestion()) > 0
                ? payload.numberQuestion()
                : listQuestion.size();
        // shuffle list question
        Collections.shuffle(listQuestion);
        // get random question
        listQuestion = listQuestion.subList(0, maxQuestion);

        var paramExam = new MapSqlParameterSource().addValue("examName", payload.name())
                                                   .addValue("hasMonitor", payload.hasMonitor())
                                                   .addValue("numberOfQuestion", maxQuestion)
                                                   .addValue("startDate", payload.startDate())
                                                   .addValue("endDate", payload.endDate())
                                                   .addValue("subjectId", payload.subjectId());
        var examId = writeDb.queryForObject(
                "CALL up_SaveExam(:examName, :hasMonitor, :numberOfQuestion, :subjectId, :startDate, :endDate)",
                paramExam,
                Long.class
        );
        var index = new AtomicInteger(1);
        var paramQuestion = listQuestion
                .stream()
                .map(q -> q.toMap(index.getAndIncrement(), examId))
                .toList()
                .toArray(new MapSqlParameterSource[0]);
        writeDb.batchUpdate(
                "CALL up_SaveQuestion(:no, :image, :content, :optionA, :optionB, :optionC, :optionD, :answer, :examId)",
                paramQuestion
        );

        return Constants.SUCCESS;
    }
}
