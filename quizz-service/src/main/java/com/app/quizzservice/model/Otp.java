package com.app.quizzservice.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public record Otp(
        long otpId,
        String email,
        String otp,
        long expiredAt,
        Date createdAt
) {
    public Otp(ResultSet rs) throws SQLException {
        this(
                rs.getLong("otp_id"),
                rs.getString("email"),
                rs.getString("otp"),
                rs.getLong("expired_at"),
                rs.getDate("created_at")
        );
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiredAt();
    }

    public boolean isCorrect(String otp) {
        return this.otp().equals(otp);
    }

    public boolean isNotExpired() {
        return !isExpired();
    }

    public boolean isNotCorrect(String otp) {
        return !isCorrect(otp);
    }

    public boolean isValid(String otp) {
        return this.otp().equals(otp) && !isExpired();
    }
}
