package com.app.quizzservice.security;

import com.app.quizzservice.config.SystemConfigService;
import com.app.quizzservice.jwt.JwtAuthenticationFilter;
import com.app.quizzservice.security.oauth2.CustomOAuth2UserService;
import com.app.quizzservice.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.app.quizzservice.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.app.quizzservice.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.app.quizzservice.service.CustomerUserDetailsService;
import com.app.quizzservice.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SecurityFilterConfig {
    AuthenticationProvider authenticationProvider;
    SystemConfigService systemConfigService;
    CustomOAuth2UserService customOAuth2UserService;
    JwtAuthenticationFilter jwtAuthenticationFilter;
    OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ignoredUrls().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                        .clientRegistrationRepository(clientRegistrationRepository())
                        .redirectionEndpoint(red -> red.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
                .authenticationProvider(authenticationProvider)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(List.of("*"));
        corsConfig.setAllowedMethods(List.of("*"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.addExposedHeader(Constants.CAPTCHA);
        corsConfig.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    @ConfigurationProperties(prefix = "application.ignored.urls")
    public List<String> ignoredUrls() {
        return new ArrayList<>();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        var clientId = systemConfigService.getConfigValue(Constants.CLIENT_GOOGLE_ID);
        var clientSecret = systemConfigService.getConfigValue(Constants.CLIENT_GOOGLE_SECRET);
        return ClientRegistration.withRegistrationId(Constants.GOOGLE)
                                 .clientId(clientId)
                                 .clientSecret(clientSecret)
                                 .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                 .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                 .redirectUri("{baseUrl}/oauth2/callback/{registrationId}")
                                 .scope("profile", "email")
                                 .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                                 .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                                 .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                                 .userNameAttributeName(IdTokenClaimNames.SUB)
                                 .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                                 .clientName(StringUtils.capitalize(Constants.GOOGLE))
                                 .build();
    }
}
