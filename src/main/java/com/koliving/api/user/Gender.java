package com.koliving.api.user;

public enum Gender {
    MALE(0), FEMALE(1), OTHER(2);

    private final int code;

    public int getCode() {
        return code;
    }

    Gender(int code) {
        this.code = code;
    }
}
