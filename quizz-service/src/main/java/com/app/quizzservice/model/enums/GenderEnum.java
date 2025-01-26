package com.app.quizzservice.model.enums;

import lombok.Getter;

@Getter
public enum GenderEnum {
    MALE("male"),
    FEMALE("female");

    private final String value;
    GenderEnum(String value) {
        this.value = value;
    }
}
