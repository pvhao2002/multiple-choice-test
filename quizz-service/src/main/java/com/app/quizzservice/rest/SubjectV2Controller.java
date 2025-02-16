package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/subjects")
public class SubjectV2Controller {
    private final SubjectService subjectService;

    public SubjectV2Controller(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public Object listV2(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return ResponseContainer.success(subjectService.getSubjectsV2(page, size));
    }
}
