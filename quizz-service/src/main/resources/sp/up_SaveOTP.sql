DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveOTP //
CREATE PROCEDURE up_SaveOTP(
    IN pEmail VARCHAR(255),
    IN pOtp VARCHAR(6),
    IN pExpiredAt BIGINT
)
BEGIN
    INSERT INTO otp(email, otp, expired_at)
    VALUES (pEmail, pOtp, pExpiredAt)
    ON DUPLICATE KEY UPDATE otp = pOtp, expired_at = pExpiredAt;
END
//
