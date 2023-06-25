package com.koliving.api.vo;

import lombok.Data;

@Data
public class JwtVo {

    private String email;
    private String role;
    private String firstName;
    private String lastName;

    public String getUsername() {
        return this.getLastName() + " " + this.getFirstName();
    }
}
