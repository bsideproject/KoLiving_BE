package com.koliving.api.auth.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtVo {

    private String email;
    private String role;
    private String firstName;
    private String lastName;

    public String getUsername() {
        return this.getLastName() + " " + this.getFirstName();
    }

    public void setUsername(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Builder
    public JwtVo(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
