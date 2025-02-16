DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveExam //
CREATE PROCEDURE up_SaveExam(
    IN pExamName VARCHAR(255),
    IN pHasMonitor BOOL,
    IN pNumberOfQuestions INT,
    IN pSubjectId INT
)
BEGIN
    INSERT INTO test(name, subject_id, total_questions, status, has_monitor)
    VALUES (pExamName, pSubjectId, pNumberOfQuestions, 'ACTIVE', pHasMonitor);

    SELECT LAST_INSERT_ID() as id;
END
//
