DELIMITER //
DROP PROCEDURE IF EXISTS up_UpdateQuestion //
CREATE PROCEDURE up_UpdateQuestion(
    IN pImage LONGTEXT,
    IN pContent TEXT,
    IN pOptionA TEXT,
    IN pOptionB TEXT,
    IN pOptionC TEXT,
    IN pOptionD TEXT,
    IN pAnswer ENUM ('A', 'B', 'C', 'D'),
    IN pQuestionID INT
)
BEGIN
    UPDATE questions
    SET image    = IFNULL(pImage, image),
        content  = IFNULL(pContent, content),
        option_a = IFNULL(pOptionA, option_a),
        option_b = IFNULL(pOptionB, option_b),
        option_c = IFNULL(pOptionC, option_c),
        option_d = IFNULL(pOptionD, option_d),
        answer   = IFNULL(pAnswer, answer)
    WHERE question_id = pQuestionID;
END
//
