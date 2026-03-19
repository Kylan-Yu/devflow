package com.devflow.api.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum LanguagePreference {
    ZH_CN("zh-CN"),
    EN_US("en-US");

    private final String value;

    LanguagePreference(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static LanguagePreference fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equalsIgnoreCase(value) || item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported language preference: " + value));
    }
}
