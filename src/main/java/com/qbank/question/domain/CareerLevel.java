package com.qbank.question.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum CareerLevel {
    JUNIOR,
    YEAR_1_3,
    YEAR_3_5,
    YEAR_5_PLUS;

    private static Map<String, CareerLevel> map;

    static {
        map = new HashMap<>();
        for (CareerLevel o : values()) {
            map.put(o.name(), o);
        }
    }

    public static Optional<CareerLevel> of(String name){
        return Optional.ofNullable(map.get(name));
    }
}
