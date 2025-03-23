DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveExam //
CREATE PROCEDURE up_SaveExam(
    IN pExamName VARCHAR(255),
    IN pHasMonitor BOOL,
    IN pNumberOfQuestions INT,
    IN pSubjectId INT,
    IN pStartDate VARCHAR(255),
    IN pEndDate VARCHAR(255),
    IN pTime INT
)
BEGIN
    INSERT INTO test(name, subject_id, total_questions, status, has_monitor, start_date, end_date, `duration`)
    VALUES (pExamName, pSubjectId, pNumberOfQuestions, 'ACTIVE', pHasMonitor, pStartDate, pEndDate, pTime);

    SELECT LAST_INSERT_ID() as id;
END
//
