package com.koliving.api.base;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceError {
    RECORD_NOT_EXIST(BAD_REQUEST, "0001", "레코드 미존재"),
    INVALID_LOCATION(BAD_REQUEST, "0002", "유효하지 않은 Location 정보")

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
