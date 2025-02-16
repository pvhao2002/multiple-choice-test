DELIMITER //
DROP PROCEDURE IF EXISTS up_GetListSubject //
CREATE PROCEDURE up_GetListSubject(IN pOffset INT, IN pLimit INT)
BEGIN
    DECLARE vTotalRows INT;
    SELECT COUNT(1)
    INTO vTotalRows
    FROM subjects
    WHERE status = 'active';

    select s.subject_id
         , s.name
         , s.icon
         , count(test_id) as cnt
         , vTotalRows     as total_rows
         , s.updated_at   as last_update
    from subjects s
             left join test t
                       on t.subject_id = s.subject_id and t.status = 'active'
    where true
      and s.status = 'active'
    group by s.subject_id
    order by s.subject_id desc
    limit pOffset, pLimit;
END
//
