package com.app.quizzservice.security.oauth2;

import com.app.quizzservice.exception.AppException;
import com.app.quizzservice.model.User;
import com.app.quizzservice.model.enums.ErrorCodeEnum;
import com.app.quizzservice.model.enums.RoleEnum;
import com.app.quizzservice.model.enums.StatusEnum;
import com.app.quizzservice.repo.UserRepo;
import com.app.quizzservice.security.UserPrincipal;
import com.app.quizzservice.security.oauth2.user.OAuth2UserInfo;
import com.app.quizzservice.security.oauth2.user.OAuth2UserInfoFactory;
import com.app.quizzservice.service.EmailService;
import com.app.quizzservice.utils.ImageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    UserRepo userRepo;
    PasswordEncoder passwordEncoder;
    EmailService emailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        var oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration()
                                 .getRegistrationId(),
                oAuth2User.getAttributes()
        );
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new AppException(ErrorCodeEnum.UN_AUTHORIZATION);
        }

        var userOptional = userRepo.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (StatusEnum.BLOCKED.equals(user.getStatus())) {
                throw new AppException(ErrorCodeEnum.ACCOUNT_LOCKED);
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserInfo);
        }

        return UserPrincipal.create(user);
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        try {
            var password = UUID.randomUUID().toString();
            var user = User
                    .builder()
                    .firstName(oAuth2UserInfo.getFirstName())
                    .lastName(oAuth2UserInfo.getLastName())
                    .email(oAuth2UserInfo.getEmail())
                    .avatar(ImageUtils.getBase64FromImageUrl(oAuth2UserInfo.getImageUrl()))
                    .status(StatusEnum.ACTIVE)
                    .password(passwordEncoder.encode(password))
                    .build();
            var returnUser = userRepo.save(user, true);
            emailService.sendEmailWelcome(returnUser);
            return returnUser;
        } catch (Exception e) {
            throw new AppException(ErrorCodeEnum.REGISTER_FAILED);
        }
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setAvatar(ImageUtils.getBase64FromImageUrl(oAuth2UserInfo.getImageUrl()));
        existingUser.setStatus(StatusEnum.INACTIVE.equals(existingUser.getStatus()) ? StatusEnum.ACTIVE : existingUser.getStatus());
        return userRepo.save(existingUser, false);
    }
}
