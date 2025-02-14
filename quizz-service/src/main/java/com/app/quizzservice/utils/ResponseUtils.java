package com.app.quizzservice.utils;

import com.app.quizzservice.model.ResponseContainer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@UtilityClass
public class ResponseUtils {

    public static void result(HttpServletResponse response, ResponseContainer<?> container) throws IOException {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(container.getStatus());
        response.getWriter().write(JsonConverter.convertObjectToJson(container));
        response.getWriter().flush();
        response.getWriter().close();
    }

}
