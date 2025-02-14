package com.app.quizzservice.repo;

import com.app.quizzservice.config.db.ReadDB;
import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.UserToken;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserTokenRepo {
    private final NamedParameterJdbcTemplate writeDb;
    private final NamedParameterJdbcTemplate readDb;

    public UserTokenRepo(@WriteDB NamedParameterJdbcTemplate writeDb, @ReadDB NamedParameterJdbcTemplate readDb) {
        this.writeDb = writeDb;
        this.readDb = readDb;
    }

    public void save(UserToken token) {
        var params = new MapSqlParameterSource()
                .addValue("user_id", token.userId())
                .addValue("token", token.token())
                .addValue("created_at", token.createdAt())
                .addValue("expired_at", token.expiredAt());
        writeDb.update(
                "CALL up_SaveToken(:user_id, :token, :created_at, :expired_at)",
                params
        );
    }
}
