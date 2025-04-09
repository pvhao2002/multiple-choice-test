package com.app.quizzservice.service;

import com.app.quizzservice.model.Course;
import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.Test;
import com.app.quizzservice.model.User;
import com.app.quizzservice.request.dto.CourseDTO;
import com.app.quizzservice.request.dto.CourseDetail;
import com.app.quizzservice.request.payload.CourseAboutPayload;
import com.app.quizzservice.request.response.CourseResponse;
import com.app.quizzservice.request.response.SubjectCourseRepsonse;
import com.app.quizzservice.utils.PagingUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final NamedParameterJdbcTemplate writeDb;

    public CourseService(NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }

    public List<CourseDTO> exportCourse(long courseId) {
        var sql = """
                select t.test_id,
                       t.name               as testName,
                       u.email,
                       t.total_questions,
                       ta.created_at        as testDate,
                       ta.total_correct,
                       ta.number_of_warning as totalWarning,
                       ta.updated_at        as lastUpdate,
                       t.has_monitor
                from course c
                         inner join course_test ct on c.course_id = ct.course_id
                         inner join test t on ct.test_id = t.test_id
                         inner join test_attempts ta on t.test_id = ta.test_id
                         inner join users u on ta.user_id = u.user_id
                where c.course_id = :courseId;
                """;
        return writeDb.query(
                sql,
                Map.of("courseId", courseId),
                BeanPropertyRowMapper.newInstance(CourseDTO.class)
        );
    }

    public CourseAboutPayload about() {
        var sql = """
                select course_id as aboutId,
                       course_description as content
                from course_about
                """;
        return writeDb.query(
                sql,
                Map.of(),
                (res, i) -> new CourseAboutPayload(res)
        ).stream().findFirst().orElse(new CourseAboutPayload(1L, ""));
    }

    @Transactional
    public void updateAbout(Long id, String content) {
        var sql = """
                INSERT INTO course_about (course_id, course_description)
                VALUES (:id, :content)
                ON DUPLICATE KEY UPDATE course_description = :content;
                """;
        var params = Map.of("id", id, "content", content);
        writeDb.update(sql, params);
    }


    public CourseResponse detail(long cid, long userId) {
        var sql = """
                 select t1.course_id,
                       t1.course_code,
                       t3.test_id,
                       t3.name,
                       t3.start_date,
                       t3.end_date,
                       t4.subject_id,
                       t4.name                                              as subject_name,
                       t4.icon                                              as subject_icon,
                       t3.duration,
                       IFNULL((select exists(select 1
                                      from test_attempts ta
                                      where ta.user_id = :userId
                                        and ta.test_id = t3.test_id)),0)       as is_attempted
                from course t1
                         inner join course_test t2 on t2.course_id = t1.course_id
                         inner join test t3 on t3.test_id = t2.test_id
                         inner join subjects t4 on t3.subject_id = t4.subject_id
                where t1.course_id = :courseId;
                 """;
        var data = writeDb.query(
                sql,
                Map.of("courseId", cid, "userId", userId),
                BeanPropertyRowMapper.newInstance(CourseDetail.class)
        );
        var subjects = data.stream()
                           .collect(Collectors.groupingBy(CourseDetail::getSubjectId))
                           .entrySet().stream()
                           .map(SubjectCourseRepsonse::new)
                           .toList();

        return new CourseResponse(cid, data.getFirst().getCourseCode(), subjects);
    }

    public PagingContainer<Course> findByStudent(String studentId, int page, int size) {
        var offset = PagingUtil.calculateOffset(page, size);
        var sql = """
                select c.*,
                       count(distinct ct.test_id) as total_test
                from course_detail cd
                         inner join course c on c.course_id = cd.course_id and c.status = 'active'
                         left join course_test ct on ct.course_id = c.course_id
                where cd.student_id = :studentId
                group by c.course_id, c.course_code
                limit :size offset :offset;
                """;
        var data = writeDb.query(
                sql,
                Map.of("studentId", studentId, "size", size, "offset", offset),
                BeanPropertyRowMapper.newInstance(Course.class)
        );

        var totalRecords = writeDb.queryForObject(
                """
                        select count(1)
                        from course_detail cd
                                 inner join course c on c.course_id = cd.course_id and c.status = 'active'
                        where cd.student_id = :studentId;
                        """,
                Map.of("studentId", studentId),
                Long.class
        );
        return new PagingContainer<>(page, size, totalRecords, data);
    }

    @Transactional
    public void removeExam(long courseId, long examId) {
        writeDb.update(
                "DELETE FROM course_test WHERE course_id = :courseId AND test_id = :examId",
                Map.of("courseId", courseId, "examId", examId)
        );
    }

    @Transactional
    public List<String> addExam2Course(long courseId, List<Long> examIds) {
        writeDb.update("DROP TABLE IF EXISTS temp_exam;", Map.of());
        writeDb.update("CREATE TABLE IF NOT EXISTS temp_exam(exam_id INT, exam_name VARCHAR(255));", Map.of());
        var params = examIds.stream()
                            .map(examId -> new MapSqlParameterSource()
                                    .addValue("examId", examId)
                                    .addValue("courseId", courseId))
                            .toArray(MapSqlParameterSource[]::new);
        writeDb.batchUpdate(
                "CALL up_AddExamToCourse(:courseId, :examId)",
                params
        );

        var listImportFail = writeDb.query(
                "SELECT * FROM temp_exam",
                Map.of(),
                (rs, i) -> rs.getString("exam_name")
        );
        writeDb.update("DROP TABLE IF EXISTS temp_exam", Map.of());
        return listImportFail;
    }

    public PagingContainer<Test> getExams(long courseId, int page, int size) {
        var data = writeDb.query(
                """
                        SELECT t2.*
                        FROM course_test t1
                            INNER JOIN test t2 ON t1.test_id = t2.test_id
                        WHERE t1.course_id = :courseId
                        LIMIT :size OFFSET :offset
                        """,
                Map.of("courseId", courseId, "size", size, "offset", PagingUtil.calculateOffset(page, size)),
                (rs, i) -> new Test(rs)
        );

        var totalRecords = writeDb.queryForObject(
                "SELECT COUNT(1) FROM course_test WHERE course_id = :courseId",
                Map.of("courseId", courseId),
                Long.class
        );

        return new PagingContainer<>(page, size, totalRecords, data);
    }

    @Transactional
    public void removeStudent(long courseId, String studentId) {
        writeDb.update(
                "DELETE FROM course_detail WHERE course_id = :courseId AND student_id = :studentId",
                Map.of("courseId", courseId, "studentId", studentId)
        );
    }

    @Transactional
    public List<String> addStudent2Course(long courseId, List<String> studentIds) {
        writeDb.update("DROP TABLE IF EXISTS temp_student;", Map.of());
        writeDb.update("CREATE TABLE IF NOT EXISTS temp_student(student_id VARCHAR(255));", Map.of());
        var params = studentIds.stream()
                               .map(studentId -> new MapSqlParameterSource()
                                       .addValue("studentId", studentId)
                                       .addValue("courseId", courseId))
                               .toArray(MapSqlParameterSource[]::new);
        writeDb.batchUpdate(
                "CALL up_AddStudentToCourse(:courseId, :studentId)",
                params
        );

        var listImportFail = writeDb.query(
                "SELECT * FROM temp_student",
                Map.of(),
                (rs, i) -> rs.getString("student_id")
        );
        writeDb.update("DROP TABLE IF EXISTS temp_student", Map.of());
        return listImportFail;
    }

    public PagingContainer<User> getStudent(long courseId, int page, int size) {
        var sql = """
                select t2.*
                from course_detail t1
                         inner join users t2 on t1.student_id = t2.student_id
                where t1.course_id = :courseId
                limit :size offset :offset;
                """;
        var data = writeDb.query(
                sql,
                Map.of("courseId", courseId, "size", size, "offset", PagingUtil.calculateOffset(page, size)),
                (rs, i) -> new User(rs)
        );
        var totalRecords = writeDb.queryForObject(
                "SELECT COUNT(1) FROM course_detail WHERE course_id = :courseId",
                Map.of("courseId", courseId),
                Long.class
        );
        return new PagingContainer<>(page, size, totalRecords, data);
    }

    public PagingContainer<Course> findAll(int page, int size, String code) {
        var data = writeDb.query(
                """
                        SELECT t1.*
                                , COUNT(distinct t2.student_id) as total
                                , COUNT(distinct t3.test_id) as total_test
                        FROM course t1
                                 left join course_detail t2 on t2.course_id = t1.course_id
                                 left join course_test t3 on t3.course_id = t1.course_id
                        WHERE true
                          and status = 'active'
                          and course_code LIKE :code
                        GROUP BY t1.course_id, t1.course_code
                        LIMIT :size OFFSET :offset
                                """,
                Map.of("code", "%" + code + "%", "size", size, "offset", PagingUtil.calculateOffset(page, size)),
                BeanPropertyRowMapper.newInstance(Course.class)
        );

        var totalRecords = writeDb.queryForObject(
                "SELECT COUNT(1) FROM course WHERE status = 'active' and course_code LIKE :code",
                Map.of("code", "%" + code + "%"),
                Long.class
        );

        return new PagingContainer<>(page, size, totalRecords, data);
    }

    public boolean checkExistCode(String courseCode) {
        return writeDb.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM course WHERE course_code = :courseCode)",
                Map.of("courseCode", courseCode),
                Boolean.class
        );
    }

    @Transactional
    public void createCourse(String courseCode) {
        writeDb.update(
                "INSERT INTO course (course_code) VALUES (:courseCode)",
                Map.of("courseCode", courseCode)
        );
    }

    @Transactional
    public void deleteCourse(long courseId) {
        writeDb.update(
                "update course set status = 'INACTIVE' WHERE course_id = :courseId;",
                Map.of("courseId", courseId)
        );
    }

    @Transactional
    public void updateCourse(long courseId, String newCourseCode) {
        writeDb.update(
                "update course set course_code = :newCourseCode WHERE course_id = :courseId",
                Map.of("courseId", courseId, "newCourseCode", newCourseCode)
        );
    }
}
