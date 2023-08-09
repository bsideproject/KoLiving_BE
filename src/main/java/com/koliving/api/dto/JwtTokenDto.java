package com.koliving.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @EqualsAndHashCode @ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtTokenDto {

    private final String accessToken;
    private final String refreshToken;
}
