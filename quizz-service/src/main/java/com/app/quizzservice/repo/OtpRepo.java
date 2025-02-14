package com.app.quizzservice.repo;

import com.app.quizzservice.config.db.ReadDB;
import com.app.quizzservice.config.db.WriteDB;
import com.app.quizzservice.model.Otp;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class OtpRepo {
    private final NamedParameterJdbcTemplate writeDb;
    private final NamedParameterJdbcTemplate readDb;

    public OtpRepo(@WriteDB NamedParameterJdbcTemplate writeDb, @ReadDB NamedParameterJdbcTemplate readDb) {
        this.writeDb = writeDb;
        this.readDb = readDb;
    }

    @Transactional
    public void save(Otp otp) {
        var sql = "CALL up_SaveOTP(:email, :otp, :expireTime)";
        var params = new MapSqlParameterSource()
                .addValue("email", otp.email())
                .addValue("otp", otp.otp())
                .addValue("expireTime", otp.expiredAt());
        writeDb.update(sql, params);
    }

    public Optional<Otp> findByEmail(String email) {
        var sql = "SELECT * FROM otp WHERE email = :email";
        var params = new MapSqlParameterSource().addValue("email", email);
        return readDb.query(sql, params, (rs, i) -> new Otp(rs)).stream().findFirst();
    }

    public void deleteByEmail(String email) {
        var sql = "DELETE FROM otp WHERE email = :email";
        var params = new MapSqlParameterSource().addValue("email", email);
        writeDb.update(sql, params);
    }
}
