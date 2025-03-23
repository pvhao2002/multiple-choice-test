package com.app.quizzservice.service;

import com.app.quizzservice.request.dto.ChartByExam;
import com.app.quizzservice.request.dto.UserTestWeekDTO;
import com.app.quizzservice.request.response.ChartData;
import com.app.quizzservice.request.response.Datasets;
import com.app.quizzservice.utils.Constants;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChartService {
    private final NamedParameterJdbcTemplate readDb;

    public ChartService(NamedParameterJdbcTemplate readDb) {
        this.readDb = readDb;
    }

    public ChartData totalTestByExam() {
        var sql = """
                select t.test_id,
                       t.name,
                       count(ta.test_attempt_id) as course_count
                from test t
                         inner join test_attempts ta on t.test_id = ta.test_id
                where t.status = 'active'
                group by t.test_id, t.name, t.created_at
                order by t.created_at desc
                limit 5;
                """;
        var result = readDb.query(sql, Collections.emptyMap(), BeanPropertyRowMapper.newInstance(ChartByExam.class));
        var labels = result.stream()
                           .map(ChartByExam::getName)
                           .toList();
        var series = result.stream()
                           .map(e -> {
                               var data = labels.stream()
                                                .map(label -> e.getName().equals(label) ? e.getCourseCount() : 0)
                                                .toList();
                               return new Datasets(data, e.getName());
                           })
                           .toList();
        return new ChartData(labels, series);
    }

    public ChartData getUserTestWeek(long userId) {
        var sql = """
                SELECT DATE(created_at)       AS attempt_date,
                       COUNT(test_attempt_id) AS total_attempts,
                       status_str
                FROM test_attempts
                WHERE TRUE
                    AND (:userId = -1 OR user_id = :userId)
                    AND created_at >= CURDATE() - INTERVAL 7 DAY
                GROUP BY attempt_date, status_str
                ORDER BY attempt_date DESC;
                """;
        var params = Map.of("userId", userId);
        var result = readDb.query(sql, params, (rs, i) -> new UserTestWeekDTO(rs));
        var labels = result.stream()
                           .map(UserTestWeekDTO::attemptDate)
                           .distinct()
                           .toList();
        var series = result.stream()
                           .collect(Collectors.groupingBy(UserTestWeekDTO::statusStr))
                           .entrySet()
                           .stream()
                           .map(Datasets::new)
                           .toList();
        return new ChartData(labels, series);
    }

}
