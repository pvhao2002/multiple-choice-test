package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.request.payload.SyncAnswerPayload;
import com.app.quizzservice.security.UserPrincipal;
import com.app.quizzservice.service.ExamService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("is-joined")
    public Object isJoined(
            @RequestParam(value = "eid") Long eid,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseContainer.success(examService.isJoined(eid, userPrincipal.getUserId()));
    }

    @GetMapping("detail")
    public Object detail(
            @RequestParam(value = "eid") Long eid,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseContainer.success(examService.detail(eid, userPrincipal.getUserId()));
    }

    @GetMapping("review")
    public Object review(
            @RequestParam(value = "tid") Long tid
    ) {
        return ResponseContainer.success(examService.review(tid));
    }

    @PostMapping("start-test")
    public Object startTest(
            @RequestParam(value = "eid") Long eid,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseContainer.success(examService.startTest(eid, userPrincipal.getUserId()));
    }

    @PostMapping("sync-answer")
    public Object syncAnswer(@RequestBody SyncAnswerPayload payload) {
        examService.syncAnswer(payload);
        return ResponseContainer.success("ok");
    }

    @PostMapping("submit-test")
    public Object submitTest(@RequestBody SyncAnswerPayload payload) {
        return ResponseContainer.success(examService.submitTest(payload));
    }

    @GetMapping("result")
    public Object result(
            @RequestParam(value = "page1", defaultValue = "1") Integer page,
            @RequestParam(value = "page2", defaultValue = "10") Integer page2,
            @RequestParam(value = "key", defaultValue = "") String key,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseContainer.success(examService.result(page, page2, key, userPrincipal.getUserId()));
    }
}
