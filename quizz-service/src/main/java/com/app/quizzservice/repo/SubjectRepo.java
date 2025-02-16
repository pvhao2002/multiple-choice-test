package com.app.quizzservice.repo;

import com.app.quizzservice.config.db.ReadDB;
import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.Subject;
import com.app.quizzservice.request.dto.SubjectDTO;
import com.app.quizzservice.request.response.SubjectResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SubjectRepo {
    private final NamedParameterJdbcTemplate writeDb;
    private final NamedParameterJdbcTemplate readDb;

    public SubjectRepo(@ReadDB NamedParameterJdbcTemplate writeDb, @WriteDB NamedParameterJdbcTemplate readDb) {
        this.writeDb = writeDb;
        this.readDb = readDb;
    }

    public PagingContainer<SubjectResponse> getSubjectsV2(int offset, int size) {
        var sql = "CALL up_GetListSubject(:offset, :size)";
        var params = new MapSqlParameterSource()
                .addValue("offset", offset)
                .addValue("size", size);
        var data = readDb.query(sql, params, (rs, i) -> new SubjectDTO(rs));
        var total = data.stream().findFirst().map(SubjectDTO::getTotalRow).orElse(0);
        return new PagingContainer<>(0, size, total, data.stream().map(SubjectResponse::new).toList());
    }

    public PagingContainer<Subject> getSubjects(int offset, int size, String search) {
        var sql = """
                SELECT *
                FROM subjects
                WHERE name LIKE :search AND status = 'active'
                LIMIT :size OFFSET :offset
                """;
        var params = new MapSqlParameterSource()
                .addValue("search", "%" + search + "%")
                .addValue("size", size)
                .addValue("offset", offset);
        var data = readDb.query(sql, params, (rs, i) -> new Subject(rs));
        var total = readDb.queryForObject(
                "SELECT COUNT(1) FROM subjects WHERE name LIKE :search AND status = 'active'",
                params,
                Integer.class
        );
        return new PagingContainer<>(0, size, total, data);
    }

    public void save(Subject model) {
        // save
        var params = new MapSqlParameterSource()
                .addValue("id", model.getSubjectId())
                .addValue("name", model.getName())
                .addValue("img", model.getIcon());
        var sql = "CALL up_SaveSubject(:id, :name, :img)";
        writeDb.update(sql, params);
    }

    public void delete(int id) {
        var params = new MapSqlParameterSource().addValue("id", id);
        var sql = """
                UPDATE subjects
                SET status = 'inactive'
                WHERE subject_id = :id
                """;
        writeDb.update(sql, params);
    }

    public Optional<Subject> getSubject(String sql, MapSqlParameterSource params) {
        return writeDb.query(sql, params, (rs, i) -> new Subject(rs)).stream().findFirst();
    }
}
