package com.app.quizzservice.rest;

import com.app.quizzservice.config.SystemConfigService;
import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.request.payload.GeminiPayload;
import com.app.quizzservice.service.ChatbotService;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.RequestUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatbotController {
    RestTemplate restTemplate;
    SystemConfigService systemConfigService;
    ChatbotService chatbotService;

    @GetMapping
    public Object get(@AuthenticationPrincipal User user) {
        var haveStudentId = StringUtils.isEmpty(user.getStudentId());
        if (haveStudentId) {
            return ResponseContainer.failure("Please login to use this feature!");
        }
        var histories = chatbotService.findAll(user.getStudentId());
        var contents = histories.stream()
                                .map(GeminiPayload.Content::fromHistory)
                                .flatMap(List::stream)
                                .toList();
        return ResponseContainer.success(Map.of("contents", contents, "histories", histories));
    }

    @PostMapping("ask")
    public Object ask(@RequestBody GeminiPayload payload, @AuthenticationPrincipal User user) {
        var haveStudentId = StringUtils.isNotEmpty(user.getStudentId());
        var token = systemConfigService.getConfigValue(Constants.TOKEN_GEMINI);
        var headers = RequestUtils.createHeaders();
        var httpEntity = new HttpEntity<>(payload, headers);
        var responseEntity = restTemplate.postForEntity(
                MessageFormat.format("{0}{1}", Constants.GEMINI_URL, token),
                httpEntity,
                Object.class
        );
        var question = payload.getContents().getLast().getParts().getFirst().getText();
        var time = System.currentTimeMillis();
        if (!responseEntity.getStatusCode().is2xxSuccessful()
            || !(responseEntity.getBody() instanceof Map<?, ?> m)
            || !(m.get("candidates") instanceof List<?> l)
            || !(l.getFirst() instanceof Map<?, ?> m2)
            || !(m2.get("content") instanceof Map<?, ?> c)
            || !(c.get("parts") instanceof List<?> lp && CollectionUtils.isNotEmpty(lp))
            || !(lp.getFirst() instanceof Map<?, ?> m3)
        ) {
            var response = "Sorry, I don't understand your question!";
            if (haveStudentId) {
                chatbotService.saveHistory(
                        question,
                        response,
                        user.getStudentId()
                );
            }
            return ResponseContainer.success(Map.of("time", time, "response", response));
        }
        var response = m3.get("text").toString();
        if (haveStudentId) {
            chatbotService.saveHistory(question, response, user.getStudentId());
        }
        return ResponseContainer.success(Map.of("time", time, "response", response));
    }
}
