package com.koliving.api.dto;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@RequiredArgsConstructor
public class ResponseDto<T> {

    private final int responseCode;
    private T data;
    private T error;

    public static <T> ResponseDto<T> success(T data, int responseCode) {
        ResponseDto<T> responseDto = new ResponseDto<>(responseCode);
        responseDto.setData(data);
        responseDto.setError(null);
        return responseDto;
    }

    public static <T> ResponseDto<T> failure(T error, int responseCode) {
        ResponseDto<T> responseDto = new ResponseDto<>(responseCode);
        responseDto.setData(null);
        responseDto.setError(error);
        return responseDto;
    }
}
