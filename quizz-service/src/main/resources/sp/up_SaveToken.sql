DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveToken //
CREATE PROCEDURE up_SaveToken(
    IN pUserId INT,
    IN pToken VARCHAR(500),
    IN pCreatedAt TIMESTAMP,
    IN pExpiredAt TIMESTAMP
)
BEGIN
    IF (SELECT EXISTS(SELECT 1 FROM user_tokens WHERE user_id = pUserId)) THEN
        UPDATE user_tokens
        SET token      = pToken,
            created_at = pCreatedAt,
            expired_at = pExpiredAt
        WHERE user_id = pUserId;
    ELSE
        INSERT INTO user_tokens(user_id, token, created_at, expired_at)
        VALUES (pUserId, pToken, pCreatedAt, pExpiredAt);
    END IF;
END
//


