DELIMITER //
DROP PROCEDURE IF EXISTS up_SubmitTest //
CREATE PROCEDURE up_SubmitTest(IN pTestAttemptId INT, IN pNumberWarning INT)
BEGIN
    DECLARE vScore INT DEFAULT 0;
    DECLARE vTotalCorrect INT DEFAULT 0;

    SELECT IFNULL(SUM(points), 0), COUNT(1)
    INTO vScore, vTotalCorrect
    FROM test_attempt_details
    WHERE TRUE
      AND test_attempt_id = pTestAttemptId
      AND points > 0;

    UPDATE test_attempts
    SET status_str        = 'complete',
        status            = 1,
        score             = vScore,
        total_correct     = vTotalCorrect,
        number_of_warning = pNumberWarning
    WHERE TRUE
      AND test_attempt_id = pTestAttemptId;
END
//

