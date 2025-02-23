package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.security.UserPrincipal;
import com.app.quizzservice.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("student")
    public Object getStudent(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseContainer.success(dashboardService.getStudent(page, size, userPrincipal.getUserId()));
    }


    @GetMapping("admin")
    public Object getAdam() {
        return ResponseContainer.success(dashboardService.getAdmin());
    }

    @GetMapping("user-test")
    public Object userTest(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "30") int size
    ) {
        return ResponseContainer.success(dashboardService.statisticUserTest(page, size));
    }
}
