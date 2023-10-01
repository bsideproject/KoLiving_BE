package com.koliving.api.token.blacklist;

import java.util.Date;

public record BlackAccessToken(String accessToken, Date expirationTime) {

}
