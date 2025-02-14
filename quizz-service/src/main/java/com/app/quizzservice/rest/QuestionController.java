package com.app.quizzservice.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("questions")
public class QuestionController {
    @GetMapping
    public Object list(@PathVariable String testId) {
        return null;
    }

    @PostMapping
    public Object create() {
        return null;
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
