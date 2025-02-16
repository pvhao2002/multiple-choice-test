DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveQuestion //
CREATE PROCEDURE up_SaveQuestion(
    IN pNo INT,
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
    INSERT INTO questions(test_id, no, image, content, option_a, option_b, option_c, option_d, points, answer, has_image)
    VALUES (pExamId, pNo, pImage, pContent, pOptionA, pOptionB, pOptionC, pOptionD, 1, pAnswer, IF(pImage IS NULL OR pImage = '', 0, 1));
END
//
