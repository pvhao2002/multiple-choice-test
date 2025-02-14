package com.app.quizzservice.utils;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@UtilityClass
public class CookieUtils {

    public Optional<Cookie> get(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getCookies())
                       .flatMap(cookies -> Arrays.stream(cookies).filter(c -> name.equals(c.getName())).findFirst());
    }

    public void add(HttpServletResponse response, String name, String value) {
        var cookie = new Cookie(name, value);
        cookie.setPath(Constants.SLASH);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public String add(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                             .path(Constants.SLASH)
                             .httpOnly(true)
                             .maxAge(maxAge)
                             .build().toString();
    }

    public void add(HttpServletResponse response, String name, String value, int maxAge) {
        var cookie = new Cookie(name, value);
        cookie.setPath(Constants.SLASH);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public void delete(HttpServletRequest request, HttpServletResponse response, String name) {
        Optional.ofNullable(request.getCookies())
                .ifPresent(cookies -> Arrays
                        .stream(cookies)
                        .filter(cookie -> cookie.getName().equals(name))
                        .forEach(cookie -> {
                            cookie.setValue("");
                            cookie.setPath(Constants.SLASH);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        })
                );
    }

    public String serialize(Object object) {
        return Base64.getUrlEncoder()
                     .encodeToString(SerializationUtils.serialize(object));
    }

    public <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}
