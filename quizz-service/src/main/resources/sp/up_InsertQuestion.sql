DELIMITER //
DROP PROCEDURE IF EXISTS up_InsertQuestion //
CREATE PROCEDURE up_InsertQuestion(
    IN pImage LONGTEXT,
    IN pContent VARCHAR(500),
    IN pOptionA VARCHAR(255),
    IN pOptionB VARCHAR(255),
    IN pOptionC VARCHAR(255),
    IN pOptionD VARCHAR(255),
    IN pAnswer VARCHAR(1),
    IN pExamId INT
)
BEGIN
    DECLARE vNo INT;
    SELECT MAX(no) INTO vNo FROM questions WHERE test_id = pExamId AND is_deleted = 0;
    IF vNo IS NULL THEN
        SET vNo = 0;
    END IF;

    INSERT INTO questions (test_id, no, image, content, option_a, option_b, option_c, option_d, answer, points, has_image)
    VALUES (pExamId, vNo + 1, pImage, pContent, pOptionA, pOptionB, pOptionC, pOptionD, pAnswer, 1, pImage IS NOT NULL);
END
//
