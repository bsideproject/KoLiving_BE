package com.koliving.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "응답 스키마")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDto<T> {

    @Schema(description = "response code")
    private int responseCode;

    @Schema(description = "response body (if succeed)")
    private T data;

    @Schema(description = "response body (if fail)")
    private T error;

    public static <T> ResponseDto<T> success(T data, int responseCode) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.responseCode = responseCode;
        responseDto.data = data;
        return responseDto;
    }

    public static <T> ResponseDto<T> failure(T error, int responseCode) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.responseCode = responseCode;
        responseDto.error = error;
        return responseDto;
    }
}
