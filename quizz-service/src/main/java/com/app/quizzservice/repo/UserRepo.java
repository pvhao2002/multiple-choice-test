package com.app.quizzservice.repo;

import com.app.quizzservice.config.db.ReadDB;
import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.security.UserPrincipal;
import lombok.extern.java.Log;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Log
@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate writeDb;
    private final NamedParameterJdbcTemplate readDb;

    public UserRepo(
            @WriteDB NamedParameterJdbcTemplate writeDb,
            @ReadDB NamedParameterJdbcTemplate readDb
    ) {
        this.writeDb = writeDb;
        this.readDb = readDb;
    }


    public User save(User user, boolean isRegister) {
        var params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole().name())
                .addValue("first_name", user.getFirstName())
                .addValue("gender", user.getGender().name())
                .addValue("last_name", user.getLastName())
                .addValue("avatar", user.getAvatar())
                .addValue("is_register", isRegister)
                .addValue("status", user.getStatus().name());
        var sql = "CALL up_SaveUser(:email, :password, :first_name, :last_name, :gender, :avatar, :role, :is_register, :status)";
        return getUser(sql, params).orElse(null);
    }

    public void changeStatus(Long userId, String status) {
        var sql = """
                UPDATE users
                SET status = :status
                WHERE user_id = :id
                """;
        var params = new MapSqlParameterSource()
                .addValue("id", userId)
                .addValue("status", status);
        writeDb.update(sql, params);
    }

    public List<User> findAll() {
        var sql = """
                SELECT *
                FROM users
                where role = 'student'
                """;
        return readDb.query(sql, (rs, i) -> new User(rs));
    }

    public PagingContainer<User> findAllWithPaging(int offset, int limit, String key) {
        var sql = """
                SELECT *
                FROM users
                WHERE role = 'student'
                    AND (first_name LIKE :key OR last_name LIKE :key OR email LIKE :key)
                LIMIT :limit OFFSET :offset
                """;
        var params = new MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("offset", offset)
                .addValue("key", "%" + key + "%");
        var content = readDb.query(sql, params, (rs, i) -> new User(rs));
        var totalRecords = readDb.queryForObject(
                "SELECT COUNT(1) FROM users WHERE role = 'student' AND (first_name LIKE :key OR last_name LIKE :key OR email LIKE :key)",
                new MapSqlParameterSource().addValue("key", "%" + key + "%"),
                Long.class
        );
        return new PagingContainer<>(0, limit, totalRecords, content);
    }

    public Optional<User> findById(Long id) {
        var sql = """
                SELECT *
                FROM users
                WHERE id = :id
                """;
        return getUser(sql, new MapSqlParameterSource().addValue("id", id));
    }

    public Optional<User> getById(Long id) {
        return findById(id);
    }

    public Optional<User> findByEmail(String email) {
        var sql = """
                SELECT *
                FROM users
                WHERE email = :email
                """;
        return getUser(sql, new MapSqlParameterSource().addValue("email", email));
    }

    public Optional<User> getByEmail(String email) {
        return findByEmail(email);
    }

    public Optional<UserPrincipal> findByEmailV2(String email) {
        var sql = """
                SELECT *
                FROM users
                WHERE email = :email
                """;
        return getUserPrincipal(sql, new MapSqlParameterSource().addValue("email", email));
    }

    public Optional<User> getUser(String sql, MapSqlParameterSource params) {
        return writeDb.query(sql, params, (rs, i) -> new User(rs)).stream().findFirst();
    }

    public Optional<UserPrincipal> getUserPrincipal(String sql, MapSqlParameterSource params) {
        return writeDb.query(sql, params, (rs, i) -> new UserPrincipal(rs)).stream().findFirst();
    }

    public void activateUser(String email) {
        var sql = """
                UPDATE users
                SET status = 'ACTIVE'
                WHERE email = :email
                """;
        var params = new MapSqlParameterSource().addValue("email", email);
        writeDb.update(sql, params);
    }

    public boolean checkSameToken(String token, long userId) {
        var sql = """
                SELECT EXISTS(SELECT 1
                FROM user_tokens
                WHERE TRUE
                    AND user_id = :userId
                    AND token = :token);
                """;
        var params = new MapSqlParameterSource()
                .addValue("token", token)
                .addValue("userId", userId);
        return readDb.queryForObject(sql, params, Boolean.class);
    }
}
