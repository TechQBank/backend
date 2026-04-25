package com.qbank.question.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Visibility {
    PUBLIC,
    PRIVATE;

    private static final Map<String, Visibility> map = new HashMap<>();

    static {
        for (Visibility v : values()) {
            map.put(v.name(), v);
        }
    }

    public static Optional<Visibility> of(String name) {
        return Optional.ofNullable(map.get(name));
    }
}
