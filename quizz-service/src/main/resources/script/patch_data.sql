alter table users
    add column student_id varchar(20) unique;

alter table test
    add column start_date datetime;

alter table test
    add column end_date datetime;

insert into system_settings (name, value)
VALUES ('token.gemini', 'AIzaSyBqi-65Vs0cknrw3oT4__yo2CoKkbOW8e8');


create table if not exists `course`
(
    course_id   INT auto_increment primary key,
    course_code varchar(100) not null,
    status      ENUM ('ACTIVE', 'INACTIVE') default 'ACTIVE',
    created_at  TIMESTAMP                   default CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP                   default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);

create table if not exists `course_detail`
(
    course_id  INT,
    student_id VARCHAR(20),
    PRIMARY KEY (course_id, student_id),
    constraint course_detail_course_fk FOREIGN KEY (course_id) REFERENCES course (course_id),
    constraint course_detail_user_fk FOREIGN KEY (student_id) REFERENCES users (student_id)
) charset = utf8mb3;

create table if not exists `course_test`
(
    course_id int,
    test_id   int,
    primary key (course_id, test_id),
    constraint course_test_course_id_fk
        foreign key (course_id) references course (course_id),
    constraint course_test_test_id_fk
        foreign key (test_id) references test (test_id)
);


create table if not exists `asked_questions`
(
    `id`         int auto_increment primary key,
    `student_id` varchar(20),
    `question`   text,
    `answer`     text,
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default current_timestamp on update current_timestamp,
    foreign key (`student_id`) references `users` (`student_id`)
) engine = InnoDB
  charset = utf8mb3;
