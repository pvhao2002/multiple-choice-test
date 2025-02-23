DELIMITER //
DROP PROCEDURE IF EXISTS up_GetListResult //
CREATE PROCEDURE up_GetListResult(
    IN pOffset INT
, IN pLimit INT
, IN pKeyword VARCHAR(255)
, IN pUserId INT
, IN pStatusResult VARCHAR(50)
)
BEGIN
    DECLARE vTotalRows INT DEFAULT 0;

    SELECT COUNT(1)
    FROM test_attempts ta
             INNER JOIN test t ON ta.test_id = t.test_id
    WHERE TRUE
      AND ta.user_id = pUserId
      AND ta.status_str = pStatusResult
      AND t.name LIKE CONCAT('%', pKeyword, '%')
    INTO vTotalRows;

    SELECT t.test_id,
           ta.test_attempt_id,
           t.name,
           t.has_monitor,
           ta.score,
           ta.total_correct,
           ta.status_str,
           ta.total_questions,
           ta.number_of_warning,
           vTotalRows    as total_rows,
           ta.created_at as start_time,
           ta.updated_at as finish_time
    FROM test_attempts ta
             INNER JOIN test t ON ta.test_id = t.test_id
    WHERE TRUE
      AND ta.user_id = pUserId
      AND ta.status_str = pStatusResult
      AND t.name LIKE CONCAT('%', pKeyword, '%')
    ORDER BY ta.created_at DESC
    LIMIT pOffset, pLimit;
END
//


