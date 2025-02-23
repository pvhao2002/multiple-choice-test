DELIMITER //
DROP PROCEDURE IF EXISTS up_StartTest //
CREATE PROCEDURE up_StartTest(IN pTestId INT, IN pUserId INT)
BEGIN
    DECLARE isExistNotCompletedExam BOOLEAN DEFAULT FALSE;
    DECLARE vTotalQuestions INT DEFAULT 0;
    SELECT EXISTS(SELECT 1
                  FROM test_attempts
                  WHERE TRUE
                    AND user_id = pUserId
                    AND test_id = pTestId
                    AND status = 'incomplete')
    INTO isExistNotCompletedExam;

    SELECT COUNT(1)
    INTO vTotalQuestions
    FROM questions
    WHERE test_id = pTestId;

    IF NOT isExistNotCompletedExam THEN
        INSERT INTO test_attempts(test_id, user_id, score, total_questions, total_correct, status_str, status,
                                  description, number_of_warning)
        VALUES (pTestId, pUserId, 0, vTotalQuestions, 0, 'incomplete', 0, 'Test started', 0);
        SELECT LAST_INSERT_ID() as test_attempt_id;
    END IF;
END
//
