package com.koliving.api.base;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceError {
    RECORD_NOT_EXIST(BAD_REQUEST, "0001", "레코드 미존재"),
    INVALID_LOCATION(BAD_REQUEST, "0002", "유효하지 않은 Location 정보"),
    INVALID_MONEY(BAD_REQUEST, "0003", "금액이 유효하지 않음"),
    INVALID_MAINTENANCE_FEE(BAD_REQUEST, "0004", "관리비 금액이 유효하지 않음"),
    ILLEGAL_MAINTENANCE(BAD_REQUEST, "0005", "보증금 객체 생성 유효성 실패")

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
