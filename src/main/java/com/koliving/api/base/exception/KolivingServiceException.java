package com.koliving.api.base.exception;

import com.koliving.api.base.ServiceError;
import lombok.Getter;

@Getter
public class KolivingServiceException extends RuntimeException {

    private final ServiceError error;

    public KolivingServiceException(ServiceError serviceError) {
        super(serviceError.getMessage());
        this.error = serviceError;
    }
}
