package com.app.quizzservice.request.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isAdmin,
        boolean valid
) {
}
