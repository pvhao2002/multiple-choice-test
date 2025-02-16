package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.service.ExamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/test")
public class TestV2Controller {
    private final ExamService examService;

    public TestV2Controller(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sid", defaultValue = "-1") Long sid,
            @RequestParam(value = "mode", defaultValue = "1") Integer mode
    ) {
        return ResponseContainer.success(examService.findAll(page, size, sid, mode));
    }
}
