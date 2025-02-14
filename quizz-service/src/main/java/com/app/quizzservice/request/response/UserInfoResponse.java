package com.app.quizzservice.request.response;

import com.app.quizzservice.model.User;

public record UserInfoResponse(
        String email,
        String firstName,
        String lastName,
        String gender,
        String avatar
) {
    public UserInfoResponse(User u) {
        this(u.getEmail(), u.getFirstName(), u.getLastName(), u.getGender().name(), u.getAvatar());
    }
}
