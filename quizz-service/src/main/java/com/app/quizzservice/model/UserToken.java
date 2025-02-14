package com.app.quizzservice.model;

import lombok.Builder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Builder
public record UserToken(
        long tokenId,
        long userId,
        String token,
        Date createdAt,
        Date expiredAt
) {
    public UserToken(ResultSet rs) throws SQLException {
        this(
                rs.getLong("token_id"),
                rs.getLong("user_id"),
                rs.getString("token"),
                rs.getDate("created_at"),
                rs.getDate("expired_at")
        );
    }
}
