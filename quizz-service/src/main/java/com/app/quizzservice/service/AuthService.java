package com.app.quizzservice.service;

import com.app.quizzservice.exception.AppException;
import com.app.quizzservice.jwt.JwtTokenProvider;
import com.app.quizzservice.model.User;
import com.app.quizzservice.model.enums.ErrorCodeEnum;
import com.app.quizzservice.model.enums.RoleEnum;
import com.app.quizzservice.model.enums.StatusEnum;
import com.app.quizzservice.repo.UserRepo;
import com.app.quizzservice.request.payload.RegisterPayload;
import com.app.quizzservice.request.response.LoginResponse;
import com.app.quizzservice.security.PasswordEncodeImpl;
import com.app.quizzservice.utils.AESUtils;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncodeImpl passwordEncode;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserTokenService userTokenService;

    public AuthService(
            UserRepo userRepo, PasswordEncodeImpl passwordEncode,
            JwtTokenProvider jwtTokenProvider,
            UserTokenService userTokenService
    ) {
        this.userRepo = userRepo;
        this.passwordEncode = passwordEncode;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userTokenService = userTokenService;
    }

    public LoginResponse authenticate(String username, String password) {
        var user = userRepo.findByEmailOrStudentId(username, username);
        if (user.isEmpty()) {
            throw new AppException(ErrorCodeEnum.ACCOUNT_NOT_FOUND);
        }
        validateLogin(user.get(), password);
        var userToken = jwtTokenProvider.generateToken(user.get());
        userTokenService.save(userToken);
        return new LoginResponse(
                userToken.token(),
                "",
                RoleEnum.ADMIN.equals(user.get().getRole()),
                true
        );
    }

    public void register(RegisterPayload payload) {
        var user = new User();
        user.setEmail(payload.email());
        user.setPassword(passwordEncode.encode(payload.password()));
        user.setFirstName(payload.firstName());
        user.setLastName(payload.lastName());
        user.setRole(RoleEnum.STUDENT);
        user.setStatus(StatusEnum.INACTIVE);
        userRepo.save(user, true);
    }

    public void activeUser(String email) {
        userRepo.activateUser(email);
    }

    public boolean validateEmailExist(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public boolean validateCaptcha(HttpServletRequest request, String captcha) {
        var cookie = CookieUtils.get(request, Constants.CAPTCHA);
        return cookie
                .filter(value -> value.getValue() != null)
                .map(value -> AESUtils.decrypt(value.getValue()).equals(captcha.trim()))
                .orElseGet(() -> AESUtils.decrypt(StringUtils.defaultIfBlank(
                                                 request.getHeader(Constants.CAPTCHA),
                                                 StringUtils.EMPTY
                                         ))
                                         .equals(captcha.trim()));
    }

    private void validateLogin(User user, String password) {
        if (!passwordEncode.matches(password, user.getPassword())) {
            throw new AppException(ErrorCodeEnum.PASSWORD_INCORRECT);
        }
        if (StatusEnum.BLOCKED.equals(user.getStatus())) {
            throw new AppException(ErrorCodeEnum.ACCOUNT_LOCKED);
        } else if (StatusEnum.INACTIVE.equals(user.getStatus())) {
            throw new AppException(ErrorCodeEnum.ACCOUNT_INACTIVE);
        }
    }
}
