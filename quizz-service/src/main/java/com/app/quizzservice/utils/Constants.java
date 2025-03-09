package com.app.quizzservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    // Common
    public static final String SLASH = "/";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final Character DOLLAR = '$';
    public static final String UNDERSCORE = "_";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String QUESTION_MARK = "?";
    public static final String EQUAL = "=";
    public static final String AND = "&";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CAPTCHA = "Captcha";
    public static final String AES_KEY = "quizquizquizquiz";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String CLIENT_GOOGLE_ID = "CLIENT_GOOGLE_ID";
    public static final String CLIENT_GOOGLE_SECRET = "CLIENT_GOOGLE_SECRET";
    public static final String GOOGLE = "google";
    public static final String FACEBOOK = "facebook";
    public static final String URL_FRONTEND = "url-frontend";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public static final String AUTHORIZED_REDIRECT_URI = "authorizedRedirectUris";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String USER = "user";

    // Email template
    public static final String EMAIL_OTP_TEMPLATE = "email.template.otp";
    public static final String EMAIL_RESET_PASSWORD_TEMPLATE = "email.template.reset-password";
    public static final String EMAIL_CHANGE_PASSWORD_TEMPLATE = "email.template.change-password";
    public static final String EMAIL_REGISTER_WELCOME_TEMPLATE = "email.template.register-welcome";

    // Email properties key
    public static final String EMAIL_SUBJECT_OTP = "email.subject.otp";
    public static final String EMAIL_SUBJECT_RESET_PASSWORD = "email.subject.reset-password";
    public static final String EMAIL_SUBJECT_CHANGE_PASSWORD = "email.subject.change-password";
    public static final String EMAIL_SUBJECT_REGISTER_WELCOME = "email.subject.register-welcome";
    public static final String EMAIL_HOST = "email.host";
    public static final String EMAIL_PORT = "email.port";
    public static final String EMAIL_USERNAME = "email.username";
    public static final String EMAIL_PASSWORD = "email.password";

    // email student
    public static final String DEFAULT_TAIL_EMAIL = "@eaut.edu.vn";
    public static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";
    public static final String TOKEN_GEMINI = "token.gemini";
}
