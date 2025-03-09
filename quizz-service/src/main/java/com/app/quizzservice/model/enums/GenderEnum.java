package com.app.quizzservice.model.enums;

import lombok.Getter;

@Getter
public enum GenderEnum {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return value.equalsIgnoreCase("male") ? "Nam" : "Nữ";
    }
}
