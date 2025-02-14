package com.app.quizzservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@UtilityClass
public class Base64Utils {
    public static final String BASE64_PREFIX = "data:image/png;base64,";

    public static String encode(String value) {
        return java.util.Base64.getEncoder().encodeToString(value.getBytes());
    }

    public static String decode(String value) {
        byte[] decodedValue = java.util.Base64.getDecoder().decode(value);
        return new String(decodedValue);
    }

    public static String encode(byte[] value) {
        return java.util.Base64.getEncoder().encodeToString(value);
    }

    public static byte[] decodeToBytes(String value) {
        return java.util.Base64.getDecoder().decode(value);
    }

    public static String encodeImage(byte[] value) {
        return BASE64_PREFIX + encode(value);
    }

    public static byte[] decodeImage(String value) {
        return decodeToBytes(value.replace(BASE64_PREFIX, ""));
    }

    public static String encodeImage(MultipartFile file) throws IOException {
        if (file != null && file.getContentType().startsWith("image")) {
            return encodeImage(file.getBytes());
        }
        return "";
    }
}
