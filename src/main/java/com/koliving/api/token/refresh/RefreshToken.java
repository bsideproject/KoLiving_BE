package com.koliving.api.token.refresh;

import lombok.Builder;

public record RefreshToken(String email, String token) {

}
