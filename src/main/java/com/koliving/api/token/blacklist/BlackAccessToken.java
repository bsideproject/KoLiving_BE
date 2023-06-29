package com.koliving.api.token.blacklist;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class BlackAccessToken {

    private String accessToken;
    private Date expirationTime;

    @Builder
    public BlackAccessToken(String accessToken, Date expirationTime) {
        this.accessToken = accessToken;
        this.expirationTime = expirationTime;
    }
}
