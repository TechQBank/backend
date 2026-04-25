package com.qbank.question.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Difficulty {
    EASY,
    NORMAL,
    HARD;

    private static Map<String, Difficulty> map;

    static {
        map = new HashMap<>();
        for (Difficulty o : values()) {
            map.put(o.name(), o);
        }
    }

    public static Optional<Difficulty> of(String name){
        return Optional.ofNullable(map.get(name));
    }
}
