package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.UserTestWeekDTO;

import java.util.List;
import java.util.Map;

public record Datasets(
        List<Integer> data,
        String label
) {
    public Datasets(Map.Entry<String, List<UserTestWeekDTO>> entry) {
        this(
                entry.getValue()
                     .stream()
                     .map(UserTestWeekDTO::totalAttempts)
                     .toList(),
                entry.getKey()
        );
    }
}
