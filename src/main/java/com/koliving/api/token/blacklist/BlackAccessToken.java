package com.koliving.api.token.blacklist;

import lombok.Builder;

import java.util.Date;

@Builder
public record BlackAccessToken(String accessToken, Date expirationTime) {

}
