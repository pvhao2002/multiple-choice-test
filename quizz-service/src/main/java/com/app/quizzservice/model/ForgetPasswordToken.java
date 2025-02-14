package com.app.quizzservice.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ForgetPasswordToken(
        long tokenId,
        String email,
        String token,
        long expiredAt
) {
    public ForgetPasswordToken(ResultSet rs) throws SQLException {
        this(
                rs.getLong("token_id"),
                rs.getString("email"),
                rs.getString("token"),
                rs.getLong("expired_at")
        );
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiredAt;
    }
}
