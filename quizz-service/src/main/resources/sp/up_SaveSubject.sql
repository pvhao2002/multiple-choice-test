DELIMITER //
DROP PROCEDURE IF EXISTS up_SaveSubject //
CREATE PROCEDURE up_SaveSubject(
    IN pSubjectID INT,
    IN pSubjectName VARCHAR(255),
    IN pImage LONGTEXT
)
BEGIN
    IF pSubjectID = 0 THEN
        INSERT INTO subjects (name, icon)
        VALUES (pSubjectName, pImage);
    ELSE
        UPDATE subjects
        SET name = IFNULL(pSubjectName, name),
            icon = IFNULL(pImage, icon)
        WHERE subject_id = pSubjectID;
    END IF;
END
//
