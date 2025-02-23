DELIMITER //
DROP PROCEDURE IF EXISTS up_GetExamDetail //
CREATE PROCEDURE up_GetExamDetail(IN pUserId INT, IN pTestId INT)
BEGIN
    DECLARE isExistNotCompletedExam BOOLEAN DEFAULT FALSE;
    DECLARE testAttemptId INT DEFAULT 0;
    DECLARE vNumberOfTreating INT DEFAULT 0;

    SELECT test_attempt_id, number_of_warning
    INTO testAttemptId, vNumberOfTreating
    FROM test_attempts
    WHERE TRUE
      AND user_id = pUserId
      AND test_id = pTestId
      AND status = 'incomplete'
    LIMIT 1;

    SELECT IF(testAttemptId IS NOT NULL AND testAttemptId > 0, TRUE, FALSE) INTO isExistNotCompletedExam;

    IF isExistNotCompletedExam THEN
        SELECT t.test_id,
               t.name,
               t.total_questions,
               t.has_monitor,
               q.question_id,
               q.no,
               q.image,
               q.content,
               q.option_a,
               q.option_b,
               q.option_c,
               q.option_d,
               tad.answer,
               'incomplete'      as status,
               testAttemptId     as test_attempt_id,
               vNumberOfTreating as number_treating
        FROM test t
                 INNER JOIN questions q on t.test_id = q.test_id
                 LEFT JOIN test_attempt_details tad ON tad.question_id = q.question_id AND tad.test_attempt_id = testAttemptId
        WHERE TRUE
          AND t.test_id = pTestId
          AND t.status = 'active';
    ELSE
        SELECT t.test_id,
               t.name,
               t.total_questions,
               t.has_monitor,
               q.question_id,
               q.no,
               q.image,
               q.content,
               q.option_a,
               q.option_b,
               q.option_c,
               q.option_d,
               ''            as answer,
               'not_started' as status,
               0             as test_attempt_id,
               0             as number_treating
        FROM test t
                 INNER JOIN questions q on t.test_id = q.test_id
        WHERE TRUE
          AND t.test_id = pTestId
          AND t.status = 'active';
    END IF;
END
//
