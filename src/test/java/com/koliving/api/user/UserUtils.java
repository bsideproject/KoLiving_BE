package com.koliving.api.user;

import com.koliving.api.user.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static User createUser(String dummyEmail) {
        User newUser = User.builder()
                .email(dummyEmail)
                .build();

        return newUser;
    }

    public static User createUser(String dummyEmail, String dummyPassword) {
        User newUser = User.builder()
                .email(dummyEmail)
                .build();

        newUser.setPassword(dummyPassword);
        return newUser;
    }

    public static Authentication createAuthentication(Object principal, String dummyPassword) {
        return new UsernamePasswordAuthenticationToken(principal, dummyPassword);
    }
}
