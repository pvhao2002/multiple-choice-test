package com.app.quizzservice.request.payload;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record LoginPayload(
        @NotEmpty(message = "Email/Student Id is required") String email,
        @Length(min = 6, message = "Password must be from 6 character") String password
) {
}
