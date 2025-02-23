DELIMITER //
DROP PROCEDURE IF EXISTS up_GetReview //
CREATE PROCEDURE up_GetReview(IN pTestAttemptId INT)
BEGIN
    SELECT t.test_id,
           t.has_monitor,
           t.name,
           ta.test_attempt_id,
           ta.number_of_warning,
           ta.total_correct,
           ta.score,
           ta.created_at as start_time,
           ta.updated_at as end_time,
           ta.total_questions,
           tad.question_id,
           tad.answer    as user_answer,
           tad.is_correct,
           q.no,
           q.image,
           q.content,
           q.option_a,
           q.option_b,
           q.option_c,
           q.option_d,
           q.answer      as correct_answer
    FROM test_attempts ta
             INNER JOIN test_attempt_details tad ON ta.test_attempt_id = tad.test_attempt_id
             INNER JOIN questions q ON q.question_id = tad.question_id
             INNER JOIN test t ON ta.test_id = t.test_id
    WHERE TRUE
      AND ta.test_attempt_id = pTestAttemptId;
END
//

