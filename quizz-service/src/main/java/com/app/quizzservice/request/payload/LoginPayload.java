package com.app.quizzservice.request.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record LoginPayload(
        @NotEmpty(message = "Email không được bỏ trống") @Email(message = "Email không hợp lệ") String email,
        @Length(min = 6, message = "Mật khẩu tối thiểu 6 ký tự") String password,
        @Length(min = 4, max = 4, message = "Captcha bao gồm 4 ký tự") String captcha
) {
}
