DELIMITER //
DROP PROCEDURE IF EXISTS up_GetListExam //
CREATE PROCEDURE up_GetListExam(IN pOffset INT, IN pLimit INT, IN pSubjectId INT, IN pMode INT)
BEGIN
    DECLARE vTotalRows INT;
    SELECT COUNT(1)
    INTO vTotalRows
    FROM test
    WHERE status = 'active'
      and (subject_id = pSubjectId OR pSubjectId = -1)
      and (has_monitor = pMode OR pMode = -1);

    select t.test_id
         , t.name
         , t.has_monitor
         , t.total_questions
         , count(ta.test_id) as cnt
         , t.updated_at      as last_update
         , vTotalRows        as total_rows
    from test t
             left join test_attempts ta on t.test_id = ta.test_id
    where true
      and t.status = 'active'
      and (t.subject_id = pSubjectId OR pSubjectId = -1)
      and (t.has_monitor = pMode OR pMode = -1)
    group by t.test_id
    order by t.test_id desc
    limit pOffset, pLimit;
END
//
