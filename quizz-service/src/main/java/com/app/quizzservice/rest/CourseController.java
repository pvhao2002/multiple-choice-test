package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.request.dto.CourseDTO;
import com.app.quizzservice.request.payload.AddExamCoursePayload;
import com.app.quizzservice.request.payload.AddStudentCoursePayload;
import com.app.quizzservice.request.payload.CourseAboutPayload;
import com.app.quizzservice.request.payload.CoursePayload;
import com.app.quizzservice.service.CourseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private static final List<String> EXCEL_HEADER = List.of(
            "Email",
            "Exam",
            "Has Monitor",
            "Total Question",
            "Total Correct",
            "Total Warning",
            "Last Time"
    );

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("about")
    public Object about() {
        return ResponseContainer.success(courseService.about());
    }

    @PostMapping("about")
    public Object about(@RequestBody CourseAboutPayload payload) {
        courseService.updateAbout(payload.aboutId(), payload.content());
        return ResponseContainer.success("OK");
    }

    @GetMapping("export")
    public Object exportCourse(@RequestParam("cid") long courseId) {
        try (var workbook = new XSSFWorkbook()) {
            var listUserTest = courseService.exportCourse(courseId);
            listUserTest.stream()
                        .collect(Collectors.groupingBy(CourseDTO::getTestId))
                        .forEach((key, value) -> {
                            var sheet = workbook.createSheet(value.getFirst().getTestName());
                            var headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
                            var cellStyle = workbook.createCellStyle();
                            var font = workbook.createFont();
                            font.setBold(true);
                            cellStyle.setFont(font);
                            for (int i = 0; i < EXCEL_HEADER.size(); i++) {
                                var cell = headerRow.createCell(i);
                                cell.setCellValue(EXCEL_HEADER.get(i));
                                cell.setCellStyle(cellStyle);
                            }
                            value.forEach(e -> {
                                var row = sheet.createRow(sheet.getLastRowNum() + 1);
                                row.createCell(0).setCellValue(e.getEmail());
                                row.createCell(1).setCellValue(e.getTestName());
                                row.createCell(2).setCellValue(e.getHasMonitor());
                                row.createCell(3).setCellValue(e.getTotalQuestions());
                                row.createCell(4).setCellValue(e.getTotalCorrect());
                                row.createCell(5).setCellValue(e.getTotalWarning());
                                row.createCell(6).setCellValue(e.getLastUpdate());
                            });
                            EXCEL_HEADER.forEach(e -> sheet.autoSizeColumn(EXCEL_HEADER.indexOf(e)));
                        });

            var out = new ByteArrayOutputStream();
            workbook.write(out);
            var in = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                                 .header(
                                         HttpHeaders.CONTENT_DISPOSITION,
                                         "attachment; filename=exportByCourse.xlsx"
                                 )
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                 .contentLength(in.contentLength())
                                 .body(in);
        } catch (IOException e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @GetMapping("detail")
    public Object detail(@RequestParam("cid") long cid, @AuthenticationPrincipal User user) {
        return ResponseContainer.success(courseService.detail(cid, user.getUserId()));
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
    public Object importStudent(@RequestPart("file") MultipartFile file, @RequestPart("courseId") long courseId) {
        var listStudent = new ArrayList<String>();
        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            var sheet = workbook.getSheetAt(0);
            // validate header format is: Student ID, Fullname, Gender
            var headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return ResponseContainer.failure("Sheet is empty");
            }
            sheet.forEach(row -> {
                if (row.getRowNum() < 2) return;
                listStudent.add(row.getCell(0).getStringCellValue());
            });
            var result = courseService.addStudent2Course(courseId, listStudent);

            return CollectionUtils.isEmpty(result)
                    ? ResponseContainer.success("OK")
                    : ResponseContainer.failure(result);
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
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
