DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveUser //
CREATE PROCEDURE up_SaveUser(
    IN pEmail VARCHAR(255),
    IN pPassword VARCHAR(255),
    IN pFirstName VARCHAR(255),
    IN pLastName VARCHAR(255),
    IN pGender varchar(10),
    IN pAvatar TEXT,
    IN pRole ENUM ('Student'),
    IN pRegister TINYINT(1),
    IN pStatus VARCHAR(10)
)
BEGIN
    DECLARE vUserID INT;
    SELECT user_id
    INTO vUserID
    FROM users
    WHERE email = pEmail;
    IF vUserID IS NULL THEN
        INSERT INTO users(email, password, first_name, last_name, gender, avatar, role, status)
        VALUES (pEmail, pPassword, IFNULL(pFirstName, ''), IFNULL(pLastName, ''), IFNULL(pGender, 'other'), pAvatar, pRole, IFNULL(pStatus, 'INACTIVE'));
        SET vUserID = LAST_INSERT_ID();
    ELSEIF NOT pRegister THEN
        UPDATE users
        SET password   = pPassword,
            first_name = IFNULL(pFirstName, first_name),
            last_name  = IFNULL(pLastName, last_name),
            gender     = IFNULL(pGender, gender),
            avatar     = IFNULL(pAvatar, avatar),
            updated_at = NOW(),
            status     = IFNULL(pStatus, status)
        WHERE user_id = vUserID;
    END IF;
    SELECT *
    FROM users
    WHERE user_id = vUserID;
END
//
