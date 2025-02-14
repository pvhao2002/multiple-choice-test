package com.app.quizzservice.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncodeImpl implements PasswordEncoder {

    private static final BCryptPasswordEncoder ENCRYPTOR = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return ENCRYPTOR.encode(String.valueOf(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return ENCRYPTOR.matches(String.valueOf(rawPassword), encodedPassword);
    }

}
