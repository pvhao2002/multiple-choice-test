package com.app.quizzservice.security.oauth2;

import com.app.quizzservice.config.SystemConfigService;
import com.app.quizzservice.exception.AppException;
import com.app.quizzservice.jwt.JwtTokenProvider;
import com.app.quizzservice.model.enums.ErrorCodeEnum;
import com.app.quizzservice.repo.UserRepo;
import com.app.quizzservice.service.UserTokenService;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.logging.Level;

@Log
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    JwtTokenProvider tokenProvider;
    UserRepo userRepo;
    SystemConfigService systemConfigService;
    UserTokenService userTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.log(Level.FINE, "Response has already been committed. Unable to redirect to {0}", targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        var redirectUri = CookieUtils.get(request, Constants.REDIRECT_URI_PARAM_COOKIE_NAME)
                                     .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new AppException(ErrorCodeEnum.UN_AUTHORIZATION);
        }

        var targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        var user = userRepo.findByEmail(authentication.getName())
                           .orElseThrow(() -> new AppException(
                                   ErrorCodeEnum.ACCOUNT_NOT_FOUND
                           ));
        var token = tokenProvider.generateToken(user);

        userTokenService.save(token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                                   .path("/#/oauth2/redirect")
                                   .queryParam("token", token.token())
                                   .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        var clientRedirectUri = URI.create(uri);
        var systemRedirectUri = systemConfigService.getConfigValue(Constants.AUTHORIZED_REDIRECT_URI);
        if (StringUtils.isBlank(systemRedirectUri)) {
            return true;
        }
        var listRedirectUri = systemRedirectUri.split(Constants.COMMA);
        return Arrays.stream(listRedirectUri)
                     .anyMatch(authorizedRedirectUri -> {
                         var authorizedUri = URI.create(authorizedRedirectUri);
                         return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                                && authorizedUri.getPort() == clientRedirectUri.getPort();
                     });
    }
}
