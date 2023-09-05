package com.koliving.api.user;

class UserUtils {

    public static User createUser(String dummyEmail, String dummyPassword) {
        User newUser = User.builder()
                .email(dummyEmail)
                .build();

        newUser.setPassword(dummyPassword);
        return newUser;
    }
}
