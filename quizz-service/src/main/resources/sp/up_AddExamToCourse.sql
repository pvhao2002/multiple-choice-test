DELIMITER //
DROP PROCEDURE IF EXISTS up_AddExamToCourse //
CREATE PROCEDURE up_AddExamToCourse(
    IN pCourseId INT,
    IN pExamId INT
)
BEGIN
    IF (SELECT EXISTS(SELECT 1 FROM course_test where course_id = pCourseId and test_id = pExamId)) THEN
        INSERT INTO temp_exam(exam_id, exam_name) select test_id, name from test where test_id = pExamId;
    ELSE
        INSERT INTO course_test(course_id, test_id)
        VALUES (pCourseId, pExamId);
    END IF;
END
//
