package com.koliving.api.base.exception;

import com.koliving.api.base.ServiceError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KolivingServiceException extends RuntimeException {

    private final ServiceError error;

    public HttpStatus getStatus() {
        return error.getStatus();
    }

    public KolivingServiceException(ServiceError serviceError) {
        super(serviceError.getMessage());
        this.error = serviceError;
    }
}
