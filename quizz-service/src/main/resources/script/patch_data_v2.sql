drop table if exists `course_about`;
create table if not exists `course_about`
(
    course_id          int primary key,
    course_description longtext
);
insert into course_about(course_id, course_description) VALUES (1, '');
alter table test add column `duration` int(11) default 0;
