package com.koliving.api.user;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Gender {
    MALE(0), FEMALE(1), OTHER(2);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    private final static Map<Integer, Gender> map;
    static {
        map = new HashMap<>();
        for (Gender gender : Gender.values()) {
            map.put(gender.getCode(), gender);
        }
    }

    public static Gender findByCode(int code) {
        return map.get(code);
    }
}
