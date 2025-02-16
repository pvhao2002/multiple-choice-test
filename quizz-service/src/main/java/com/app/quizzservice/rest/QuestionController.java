package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.request.payload.AddQuestion;
import com.app.quizzservice.service.QuestionService;
import com.app.quizzservice.utils.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public Object list(@PathVariable String testId) {
        return null;
    }

    @PostMapping
    public Object create(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "question") AddQuestion question
    ) {
        String url = null;
        try {
            if (file != null && !file.isEmpty()) {
                url = Base64Utils.encodeImage(file);
            }
            questionService.insert(question, url);
            return ResponseContainer.success("Question created successfully");
        } catch (Exception e) {
            return ResponseContainer.failure("Failed to create question");
        }
    }

    @PatchMapping
    public Object update(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "question") AddQuestion question
    ) {
        String imageUrl = null;
        try {
            if (file != null && !file.isEmpty()) {
                imageUrl = Base64Utils.encodeImage(file);
            }
            questionService.save(question, imageUrl);
            return ResponseContainer.success("Image uploaded successfully");
        } catch (Exception exp) {
            return ResponseContainer.failure("Failed to upload image");
        }
    }

    @DeleteMapping
    public Object delete(@RequestParam("qid") Long qid) {
        return ResponseContainer.success(questionService.delete(qid));
    }
}
