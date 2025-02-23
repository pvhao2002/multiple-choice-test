package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.security.UserPrincipal;
import com.app.quizzservice.service.ChartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chart")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("user-test-week")
    public Object getUserTestWeek() {
        return ResponseContainer.success(chartService.getUserTestWeek(-1));
    }

    @GetMapping("my-test-week")
    public Object myTestWeek(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseContainer.success(chartService.getUserTestWeek(userPrincipal.getUserId()));
    }
}
