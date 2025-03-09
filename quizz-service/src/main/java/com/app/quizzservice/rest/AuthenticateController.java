package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.request.payload.LoginPayload;
import com.app.quizzservice.request.payload.OtpVerifyPayload;
import com.app.quizzservice.request.payload.RegisterPayload;
import com.app.quizzservice.service.AuthService;
import com.app.quizzservice.service.EmailService;
import com.app.quizzservice.service.OtpService;
import com.app.quizzservice.utils.AESUtils;
import com.app.quizzservice.utils.CaptchaGenerator;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    private final AuthService authService;
    private final EmailService emailService;
    private final OtpService otpService;

    public AuthenticateController(AuthService authService, EmailService emailService, OtpService otpService) {
        this.authService = authService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @GetMapping(value = "captcha", produces = MediaType.IMAGE_JPEG_VALUE)
    public Object getCaptcha(HttpServletResponse response) {
        var captchaProperty = CaptchaGenerator.getCaptchaProperty();
        var minutes = 60 * 5;
        CookieUtils.add(response, Constants.CAPTCHA, AESUtils.encrypt(captchaProperty.answer()), minutes);
        response.setHeader(Constants.CAPTCHA, AESUtils.encrypt(captchaProperty.answer()));
        return captchaProperty.captcha();
    }

    @PostMapping("login")
    public Object login(
            @Valid @RequestBody LoginPayload payload,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (!authService.validateCaptcha(request, payload.captcha())) {
            return ResponseContainer.failure("Invalid captcha");
        }
        CookieUtils.delete(request, response, Constants.CAPTCHA);
        return ResponseContainer.success(authService.authenticate(payload.email(), payload.password()));
    }

    @PostMapping("register")
    public Object register(@Valid @RequestBody RegisterPayload payload) {
        try {
            if (authService.validateEmailExist(payload.email())) {
                return ResponseContainer.failure("Email already exists");
            }
            authService.register(payload);
            emailService.sendEmailOtp(payload.email());
            return ResponseContainer.success("Register successfully");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("otp-resend")
    public Object resendOtp(@RequestBody String email) {
        try {
            emailService.sendEmailOtp(email);
            return ResponseContainer.success("Send OTP successfully");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("forget-password")
    public Object forgetPassword(@RequestBody String email) {
        try {
            var key = "%s|%s".formatted(email, System.currentTimeMillis());
            var encryptedKey = AESUtils.encrypt(key);
            return ResponseContainer.success("Send OTP successfully");
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @PostMapping("otp-verify")
    public Object verifyOtp(@RequestBody OtpVerifyPayload payload) {
        var otp = otpService.getOtp(payload.email());
        if (otp.isEmpty() || otp.get().isNotCorrect(payload.otp())) {
            return ResponseContainer.failure("Invalid OTP");
        }
        if (otp.get().isExpired()) {
            return ResponseContainer.failure("OTP is expired");
        }
        otpService.deleteOtp(payload.email());
        authService.activeUser(payload.email());
        return ResponseContainer.success("OTP is correct");
    }
}
