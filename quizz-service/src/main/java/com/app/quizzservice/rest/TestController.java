package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.request.payload.*;
import com.app.quizzservice.service.ExamService;
import com.app.quizzservice.service.QuestionService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {
    private final ExamService examService;
    private final QuestionService questionService;

    public TestController(ExamService examService, QuestionService questionService) {
        this.examService = examService;
        this.questionService = questionService;
    }

    @GetMapping
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "key", defaultValue = "") String key
    ) {
        return ResponseContainer.success(examService.findAll(page, size, key));
    }

    @PostMapping(value = "import-excel", consumes = {"multipart/form-data"})
    @Transactional
    public Object importExcel(
            @RequestPart("file") MultipartFile file,
            @RequestPart("payload") ImportExcelPayload payload
    ) {
        var listQuestion = new ArrayList<ImportExcelQuetion>();
        var examId = examService.save(payload.name(), payload.hasMonitor(), payload.subjectId(), payload.startDate(), payload.endDate());
        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);
                sheet.forEach(row -> {
                    if (row.getRowNum() == 0) return;
                    var question = new ImportExcelQuetion(row, examId);
                    listQuestion.add(question);
                });
            }
            questionService.save(listQuestion);
            examService.correctDataExam(examId);
            return ResponseContainer.success("Import excel successfully");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("random")
    @Transactional
    public Object randomTest(@RequestBody RandomTestPayload payload) {
        return ResponseContainer.success(examService.random(payload));
    }

    @GetMapping("detail")
    public Object list(@RequestParam("eid") Long eid) {
        return ResponseContainer.success(questionService.findByTest(eid));
    }

    @PostMapping
    public Object create(@RequestBody AddTest data) {
        return ResponseContainer.success(examService.save(data));
    }

    @PatchMapping
    public Object update(@RequestBody UpdateTestRequest request) {
        return ResponseContainer.success(examService.save(request));
    }

    @DeleteMapping
    public Object delete(@RequestParam("eid") Long eid) {
        return ResponseContainer.success(examService.delete(eid));
    }
}
