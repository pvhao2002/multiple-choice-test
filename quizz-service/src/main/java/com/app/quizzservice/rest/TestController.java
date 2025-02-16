package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.request.payload.AddTest;
import com.app.quizzservice.request.payload.UpdateTestRequest;
import com.app.quizzservice.service.ExamService;
import com.app.quizzservice.service.QuestionService;
import org.springframework.web.bind.annotation.*;

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
