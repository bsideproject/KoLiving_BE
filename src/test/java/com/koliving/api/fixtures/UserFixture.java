package com.koliving.api.fixtures;

import com.koliving.api.user.domain.User;

import java.util.UUID;

public class UserFixture {
    public static User createUser() {
        User user = User.builder()
            .email(String.format("%s@koliving.com", UUID.randomUUID())).build();

        return user;
    }
}
