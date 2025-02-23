DELIMITER //
DROP PROCEDURE IF EXISTS up_SyncAnswer //
CREATE PROCEDURE up_SyncAnswer(IN pTestAttemptId INT, IN pQuestionId INT, IN pAnswer VARCHAR(10))
BEGIN
    DECLARE vIsAnswerExist BOOLEAN DEFAULT FALSE;
    DECLARE vPoints INT DEFAULT 0;
    DECLARE vCorrectAnswer VARCHAR(10) DEFAULT '';

    SELECT EXISTS(SELECT 1
                  FROM test_attempt_details
                  WHERE TRUE
                    AND test_attempt_id = pTestAttemptId
                    AND question_id = pQuestionId)
    INTO vIsAnswerExist;

    SELECT points, answer
    INTO vPoints, vCorrectAnswer
    FROM questions
    WHERE TRUE
      AND question_id = pQuestionId;

    IF vIsAnswerExist THEN
        UPDATE test_attempt_details
        SET answer     = IF(pAnswer IS NULL OR pAnswer = '', NULL, pAnswer),
            points     = IF(pAnswer = vCorrectAnswer, vPoints, 0),
            is_correct = IF(pAnswer = vCorrectAnswer, 1, 0)
        WHERE TRUE
          AND test_attempt_id = pTestAttemptId
          AND question_id = pQuestionId;
    ELSE
        INSERT INTO test_attempt_details(test_attempt_id, question_id, answer, points, is_correct)
        VALUES (pTestAttemptId, pQuestionId, IF(pAnswer IS NULL OR pAnswer = '', NULL, pAnswer),
                IF(pAnswer = vCorrectAnswer, vPoints, 0),
                IF(pAnswer = vCorrectAnswer, 1, 0));
    END IF;
END
//
