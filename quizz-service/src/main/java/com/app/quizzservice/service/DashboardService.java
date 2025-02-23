package com.app.quizzservice.service;

import com.app.quizzservice.config.db.ReadDB;
import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.request.dto.UserTestDTO;
import com.app.quizzservice.request.response.DashboardAdminResponse;
import com.app.quizzservice.request.response.MyTestIncompleteResponse;
import com.app.quizzservice.utils.PagingUtil;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DashboardService {
    private final NamedParameterJdbcTemplate readDb;

    public DashboardService(@ReadDB NamedParameterJdbcTemplate readDb) {
        this.readDb = readDb;
    }

    public PagingContainer<MyTestIncompleteResponse> getStudent(int page, int size, long userId) {
        var offset = PagingUtil.calculateOffset(page, size);
        var sql = """
                select s.name                            as subject,
                       t.name                            as exam,
                       t.total_questions,
                       t.has_monitor,
                       ta.number_of_warning              as total_warning,
                       COUNT(tad.test_attempt_detail_id) as total_answer,
                       ta.updated_at as last_update
                from test_attempts ta
                         inner join test t on ta.test_id = t.test_id
                         inner join subjects s on t.subject_id = s.subject_id
                         left join test_attempt_details tad on ta.test_attempt_id = tad.test_attempt_id
                where true
                  and ta.user_id = :userId
                  and ta.status_str = 'incomplete'
                group by ta.test_attempt_id, ta.updated_at
                order by ta.updated_at desc
                limit :offset, :limit
                """;
        var sqlCount = """
                select count(1) as cnt
                from test_attempts ta
                     inner join test t on ta.test_id = t.test_id
                     inner join subjects s on t.subject_id = s.subject_id
                     left join test_attempt_details tad on ta.test_attempt_id = tad.test_attempt_id
                where true
                  and ta.user_id = :userId
                  and ta.status_str = 'incomplete'
                group by ta.test_attempt_id, ta.updated_at
                """;
        var param = new MapSqlParameterSource()
                .addValue("offset", offset)
                .addValue("limit", size)
                .addValue("userId", userId);
        var result = readDb.query(sql, param, (rs, i) -> new MyTestIncompleteResponse(rs));
        var total = readDb.query(sqlCount, Map.of("userId", userId), (rs, i) -> rs.getLong("cnt"))
                          .stream()
                          .findFirst()
                          .orElse(0L);
        return new PagingContainer<>(page, size, total, result);
    }

    public DashboardAdminResponse getAdmin() {
        var sql = """
                with t as (select count(1) as user
                           from users
                           where status = 'active'
                             and role = 'student'),
                     t1 as (select count(1) as exam
                            from test
                            where status = 'active'),
                     t2 as (select count(1) as subject
                            from subjects
                            where status = 'active')
                select *
                from t
                         inner join t1
                         inner join t2;
                """;
        return readDb.query(sql, (rs, i) -> new DashboardAdminResponse(rs)).stream().findFirst().orElse(null);
    }

    public PagingContainer<UserTestDTO> statisticUserTest(int page, int size) {
        var offset = PagingUtil.calculateOffset(page, size);
        var sql = """
                select u.email,
                       CONCAT(u.last_name, ' ', u.first_name) AS full_name,
                       s.name                            as subject,
                       t.name                            as test_name,
                       t.has_monitor,
                       ta.total_questions,
                       ta.total_correct,
                       ta.status_str,
                       ta.number_of_warning,
                       ta.updated_at as last_update
                from test_attempts ta
                         inner join users u on ta.user_id = u.user_id
                         inner join test t on ta.test_id = t.test_id
                         inner join subjects s on t.subject_id = s.subject_id
                order by ta.updated_at desc
                LIMIT :limit OFFSET :offset;
                """;
        var sql1 = """
                select COUNT(1)
                from test_attempts ta
                         inner join users u on ta.user_id = u.user_id
                         inner join test t on ta.test_id = t.test_id
                         inner join subjects s on t.subject_id = s.subject_id
                """;
        var param = new MapSqlParameterSource()
                .addValue("limit", size)
                .addValue("offset", offset);
        var result = readDb.query(sql, param, (rs, i) -> new UserTestDTO(rs));
        var totalRows = readDb.queryForObject(sql1, param, Integer.class);
        return new PagingContainer<>(page, size, totalRows, result);
    }

}
