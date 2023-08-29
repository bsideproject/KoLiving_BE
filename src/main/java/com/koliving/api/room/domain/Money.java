package com.koliving.api.room.domain;

import static lombok.AccessLevel.PROTECTED;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode(of = "amount")
@NoArgsConstructor(access = PROTECTED)
public class Money {

    @Transient
    public static final int MIN_AMOUNT = 0;

    @Column
    private Integer amount;

    private Money(int amount) {
        validate(amount);
        this.amount = amount;
    }

    public static Money valueOf(Integer amount) {
        if (Objects.isNull(amount)) {
            return empty();
        }

        return new Money(amount);
    }

    public static Money empty() {
        return new Money(MIN_AMOUNT);
    }

    private void validate(int amount) {
        if (amount < MIN_AMOUNT) {
            throw new KolivingServiceException(ServiceError.INVALID_MONEY);
        }
    }

    public int value() {
        return this.amount;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.amount == MIN_AMOUNT;
    }
}
