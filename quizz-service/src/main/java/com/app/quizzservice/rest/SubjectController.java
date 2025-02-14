package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.Subject;
import com.app.quizzservice.service.SubjectService;
import com.app.quizzservice.utils.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("subjects")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "key", defaultValue = "") String key
    ) {
        return ResponseContainer.success(subjectService.getSubjects(page, size, key));
    }

    @PostMapping
    public Object create(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseContainer.success(subjectService.save(file, name));
    }

    @PatchMapping
    public Object update() {
        return null;
    }

    @DeleteMapping
    public Object delete() {
        return null;
    }
}
