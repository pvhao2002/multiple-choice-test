package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.request.payload.AddExamCoursePayload;
import com.app.quizzservice.request.payload.AddStudentCoursePayload;
import com.app.quizzservice.request.payload.CoursePayload;
import com.app.quizzservice.service.CourseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("detail")
    public Object detail(@RequestParam("cid") long cid) {
        return ResponseContainer.success(courseService.detail(cid));
    }

    @GetMapping("student")
    public Object getCoursesByStudent(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseContainer.success(courseService.findByStudent(user.getStudentId(), page, size));
    }

    @GetMapping
    public Object getCourses(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "code", defaultValue = "") String code
    ) {
        return ResponseContainer.success(courseService.findAll(page, size, code));
    }

    @GetMapping("{courseId}/exam")
    public Object getExams(
            @PathVariable("courseId") long courseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseContainer.success(courseService.getExams(courseId, page, size));
    }

    @PostMapping("add-exam")
    public Object addExam(@RequestBody AddExamCoursePayload payload) {
        var result = courseService.addExam2Course(payload.courseId(), payload.examIds());
        return CollectionUtils.isEmpty(result)
                ? ResponseContainer.success("OK")
                : ResponseContainer.failure(result);
    }

    @DeleteMapping("{courseId}/exam/{examId}")
    public Object removeExam(@PathVariable("courseId") long courseId, @PathVariable("examId") long examId) {
        courseService.removeExam(courseId, examId);
        return ResponseContainer.success("OK");
    }


    @GetMapping("/{courseId}/student")
    public Object getStudents(
            @PathVariable("courseId") long courseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseContainer.success(courseService.getStudent(courseId, page, size));
    }


    @PostMapping("add-student")
    public Object addStudent(@RequestBody AddStudentCoursePayload payload) {
        var result = courseService.addStudent2Course(payload.courseId(), payload.studentIds());
        return CollectionUtils.isEmpty(result)
                ? ResponseContainer.success("OK")
                : ResponseContainer.failure(result);
    }

    @DeleteMapping("{courseId}/student/{studentId}")
    public Object removeStudent(@PathVariable("courseId") long courseId, @PathVariable("studentId") String studentId) {
        courseService.removeStudent(courseId, studentId);
        return ResponseContainer.success("OK");
    }

    @PostMapping("import-student")
    public Object importStudent(@RequestParam("file") MultipartFile file) {

        return ResponseContainer.success("OK");
    }

    @PostMapping
    public Object create(@RequestBody CoursePayload payload) {
        if (courseService.checkExistCode(payload.courseCode())) {
            return ResponseContainer.failure("Course code already exists");
        }
        courseService.createCourse(payload.courseCode());
        return ResponseContainer.success("OK");
    }

    @PatchMapping
    public Object update(@RequestBody CoursePayload payload) {
        courseService.updateCourse(payload.courseId(), payload.courseCode());
        return ResponseContainer.success("OK");
    }

    @DeleteMapping("/{courseId}")
    public Object delete(@PathVariable("courseId") long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseContainer.success("OK");
    }
}
