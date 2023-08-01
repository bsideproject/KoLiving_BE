package com.koliving.api.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @EqualsAndHashCode @ToString
public class TokenDto {

    private final String accessToken;
    private final String refreshToken;
}
