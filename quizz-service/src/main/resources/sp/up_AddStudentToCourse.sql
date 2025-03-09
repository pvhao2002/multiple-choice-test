DELIMITER //
DROP PROCEDURE IF EXISTS up_AddStudentToCourse //
CREATE PROCEDURE up_AddStudentToCourse(
    IN pCourseId INT,
    IN pStudentId VARCHAR(20)
)
BEGIN
    IF (SELECT EXISTS(SELECT 1 FROM course_detail where course_id = pCourseId and student_id = pStudentId)) THEN
        INSERT INTO temp_student(student_id) VALUES (pStudentId);
    ELSE
        INSERT INTO course_detail(course_id, student_id)
        VALUES (pCourseId, pStudentId);
    END IF;
END
//
