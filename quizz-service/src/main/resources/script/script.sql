drop database if exists `quizz_db`;
create database if not exists `quizz_db`;
use `quizz_db`;

drop table if exists `system_settings`;
create table if not exists `system_settings`
(
    `setting_id` int(11)      not null auto_increment,
    `name`       varchar(255) not null,
    `value`      text,
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default current_timestamp on update current_timestamp,
    primary key (`setting_id`),
    unique key `name` (`name`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `users`;
create table if not exists `users`
(
    `user_id`    int(11)                 not null auto_increment,
    `email`      varchar(255)            not null,
    `password`   varchar(500)            not null,
    `first_name` varchar(50)             not null,
    `last_name`  varchar(50)             not null,
    `gender`     enum ('male', 'female', 'other') not null,
    `avatar`     LONGTEXT,
    `role`       ENUM ('admin', 'student')              default 'student',
    `status`     ENUM ('active', 'inactive', 'blocked') default 'active',
    `created_at` timestamp                              default current_timestamp,
    `updated_at` timestamp                              default current_timestamp on update current_timestamp,
    primary key (`user_id`),
    unique key `email` (`email`),
    index (`email`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `user_tokens`;
create table if not exists `user_tokens`
(
    `token_id`   int(11) not null auto_increment,
    `user_id`    int(11) not null,
    `token`      varchar(500),
    `created_at` timestamp default current_timestamp,
    `expired_at` timestamp,
    primary key (`token_id`),
    foreign key (`user_id`) references `users` (`user_id`),
    unique key (`token`),
    index (`token`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `otp`;
create table if not exists `otp`
(
    `otp_id`     int(11)      not null auto_increment,
    `email`      varchar(255) not null,
    `otp`        varchar(6)   not null,
    `expired_at` bigint,
    `created_at` timestamp default current_timestamp,
    primary key (`otp_id`),
    unique key (`email`),
    foreign key (`email`) references `users` (`email`),
    index (`email`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `forget_password_token`;
create table if not exists `forget_password_token`
(
    `token_id`  int(11) primary key auto_increment,
    `email`      varchar(255) not null,
    `token`      varchar(500) not null,
    `expired_at` bigint unsigned,
    foreign key (`email`) references `users` (`email`),
    index (`email`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `subjects`;
create table if not exists `subjects`
(
    `subject_id` int(11)      not null auto_increment,
    `name`       varchar(255) not null,
    `icon`       LONGTEXT                        default null,
    `status`     ENUM ('active', 'inactive') default 'active',
    `created_at` timestamp                   default current_timestamp,
    `updated_at` timestamp                   default current_timestamp on update current_timestamp,
    primary key (`subject_id`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;


drop table if exists `test`;
create table if not exists `test`
(
    `test_id`         int(11)      not null auto_increment,
    `subject_id`      int(11)      not null,
    `name`            varchar(255) not null,
    `total_questions` int(11)                     default 0,
    `status`          ENUM ('active', 'inactive') default 'active',
    `has_monitor`     tinyint(1)                  default 0,
    `created_at`      timestamp                   default current_timestamp,
    `updated_at`      timestamp                   default current_timestamp on update current_timestamp,
    primary key (`test_id`),
    foreign key (`subject_id`) references `subjects` (`subject_id`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;


drop table if exists `questions`;
create table if not exists `questions`
(
    `question_id` int(11)                   not null auto_increment,
    `test_id`     int(11)                   not null,
    `no`          int(11)                   not null,
    `image`       LONGTEXT,
    `has_image`   tinyint(1) default 0,
    `content`     text                      not null,
    `option_a`    text                      not null,
    `option_b`    text                      not null,
    `option_c`    text                      not null,
    `option_d`    text                      not null,
    `points`      int(11)                   not null,
    `answer`      ENUM ('a', 'b', 'c', 'd') not null,
    `is_deleted`  tinyint(1)                default 0,
    `created_at`  timestamp  default current_timestamp,
    `updated_at`  timestamp  default current_timestamp on update current_timestamp,
    primary key (`question_id`),
    foreign key (`test_id`) references `test` (`test_id`)
) engine = InnoDB
  default charset = utf8;


drop table if exists `test_attempts`;
create table if not exists `test_attempts`
(
    `test_attempt_id`   int(11) not null auto_increment,
    `test_id`           int(11) not null,
    `user_id`           int(11) not null,
    `score`             int(11),
    `total_questions`   int(11) not null,
    `total_correct`     int(11),
    `status_str`        enum ('incomplete', 'complete') default 'incomplete',
    `status`            tinyint(1)                      default 0,
    `description`       text,
    `number_of_warning` int(11)                         default 0,
    `created_at`        timestamp                       default current_timestamp,
    `updated_at`        timestamp                       default current_timestamp on update current_timestamp,
    primary key (`test_attempt_id`),
    foreign key (`test_id`) references `test` (`test_id`),
    foreign key (`user_id`) references `users` (`user_id`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `test_attempt_details`;
create table if not exists `test_attempt_details`
(
    `test_attempt_detail_id` int(11)                   not null auto_increment,
    `test_attempt_id`        int(11)                   not null,
    `question_id`            int(11)                   not null,
    `answer`                 ENUM ('a', 'b', 'c', 'd'),
    `points`                 int(11),
    `is_correct`             tinyint(1),
    `created_at`             timestamp default current_timestamp,
    `updated_at`             timestamp default current_timestamp on update current_timestamp,
    primary key (`test_attempt_detail_id`),
    foreign key (`test_attempt_id`) references `test_attempts` (`test_attempt_id`),
    foreign key (`question_id`) references `questions` (`question_id`)
) engine = InnoDB
  default charset = utf8;
