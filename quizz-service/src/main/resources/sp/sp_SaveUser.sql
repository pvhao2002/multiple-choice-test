DELIMITER //
DROP PROCEDURE IF EXISTS sp_SaveUser //
CREATE PROCEDURE sp_SaveUser(
    IN pEmail VARCHAR(255),
    IN pPassword VARCHAR(255),
    IN pFirstName VARCHAR(255),
    IN pLastName VARCHAR(255),
    IN pGender ENUM ('Male', 'Female'),
    IN pAvatar TEXT,
    IN pRole ENUM ('Admin', 'Student'),
    IN pRegister TINYINT(1)
)
BEGIN
    DECLARE vUserID INT;
    SELECT user_id
    INTO vUserID
    FROM users
    WHERE email = pEmail;
    IF vUserID IS NULL THEN
        INSERT INTO users(email, password, first_name, last_name, gender, avatar, role)
        VALUES (pEmail, pPassword, IFNULL(pFirstName, ''), IFNULL(pLastName, ''), pGender, pAvatar, pRole);
        SET vUserID = LAST_INSERT_ID();
    ELSEIF NOT pRegister THEN
        UPDATE users
        SET password   = pPassword,
            first_name = IFNULL(pFirstName, first_name),
            last_name  = IFNULL(pLastName, last_name),
            gender     = IFNULL(pGender, gender),
            avatar     = IFNULL(pAvatar, avatar),
            updated_at = NOW()
        WHERE user_id = vUserID;
    END IF;
    SELECT *
    FROM users
    WHERE user_id = vUserID;
END
//


