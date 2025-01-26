package com.app.quizzservice.repo;

import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.User;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log
@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate writeDb;

    public UserRepo(
            @WriteDB NamedParameterJdbcTemplate writeDb
    ) {
        this.writeDb = writeDb;
    }


    public User save(User user, boolean isRegister) {
        var params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole())
                .addValue("first_name", user.getFirstName())
                .addValue("gender", user.getGender())
                .addValue("last_name", user.getLastName())
                .addValue("avatar", user.getAvatar())
                .addValue("is_register", isRegister);
        var sql = "CALL up_CreateUser(:email, :password, :first_name, :last_name, :gender, :avatar, :role, :is_register)";
        return getUser(sql, params);
    }

    public User getUserById(Long id) {
        var sql = """
                SELECT *
                FROM users
                WHERE id = :id
                """;
        return getUser(sql, new MapSqlParameterSource().addValue("id", id));
    }

    public User getUserByEmail(String email) {
        var sql = """
                SELECT *
                FROM users
                WHERE email = :email
                """;
        return getUser(sql, new MapSqlParameterSource().addValue("email", email));
    }

    public User getUser(String sql, MapSqlParameterSource params) {
        return writeDb.query(sql, params, (rs, i) -> new User(rs)).stream().findFirst().orElse(null);
    }
}
