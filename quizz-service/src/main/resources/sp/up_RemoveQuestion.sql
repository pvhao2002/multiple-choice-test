DELIMITER //
DROP PROCEDURE IF EXISTS up_RemoveQuestion //
CREATE PROCEDURE up_RemoveQuestion(
    IN pQuestionId INT
)
BEGIN
    DECLARE vNo INT;
    DECLARE vTestId INT;
    SELECT no, test_id INTO vNo, vTestId FROM questions WHERE question_id = pQuestionId;

    IF vNo IS NOT NULL THEN
        UPDATE questions
        SET is_deleted = 1
        WHERE question_id = pQuestionId;

        UPDATE questions
        SET no = no - 1
        WHERE no > vNo and test_id = vTestId and is_deleted = 0;

        UPDATE test
        SET total_questions = total_questions - 1
        WHERE test_id = vTestId;
    end if;
END
//
