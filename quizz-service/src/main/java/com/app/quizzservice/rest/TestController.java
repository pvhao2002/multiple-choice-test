package com.app.quizzservice.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping
    public Object list() {
        return null;
    }

    @GetMapping("{testId}")
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
