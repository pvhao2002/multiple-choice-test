package com.app.quizzservice.request.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record RegisterPayload(
        @Email(message = "Email không hợp lệ") String email,
        @Length(min = 6, message = "Mật khẩu tối thiểu 6 ký tự") String password,
        @NotEmpty(message = "Tên không được để trống") String firstName,
        @NotEmpty(message = "Họ không được để trống") String lastName
) {
}
