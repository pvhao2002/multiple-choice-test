package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.request.response.UserInfoResponse;
import com.app.quizzservice.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("info")
    public Object info(@AuthenticationPrincipal User user) {
        return ResponseContainer.success(new UserInfoResponse(user));
    }

    @GetMapping
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseContainer.success(userService.findAll(page, size, key));
    }

    @PostMapping("change-status")
    public Object changeStatus(long userId, boolean status) {
        return ResponseContainer.success(userService.updateStatus(userId, status));
    }
}
