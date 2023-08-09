package com.koliving.api.user;

import lombok.Getter;

@Getter
public enum Gender {
    MALE(0), FEMALE(1), OTHER(2);

    private final int code;

    Gender(int code) {
        this.code = code;
    }
}
