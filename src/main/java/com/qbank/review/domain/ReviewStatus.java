package com.qbank.review.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum ReviewStatus {
    UNKNOWN,
    UNCERTAIN,
    KNOWN,
    MASTERED;

    private static final Map<String, ReviewStatus> map = new HashMap<>();

    static {
        for (ReviewStatus s : values()) {
            map.put(s.name(), s);
        }
    }

    public static Optional<ReviewStatus> of(String name) {
        return Optional.ofNullable(map.get(name));
    }
}
