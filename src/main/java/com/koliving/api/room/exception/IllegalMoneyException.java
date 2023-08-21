package com.koliving.api.room.exception;


import org.hibernate.service.spi.ServiceException;

public class IllegalMoneyException extends ServiceException {
    public static final String MESSAGE = "금액이 유효하지 않습니다.";

    public IllegalMoneyException(long amount) {
        super(String.format(MESSAGE + "  amount: %d", amount));
    }

    public IllegalMoneyException(String message) {
        super(message);
    }
}
