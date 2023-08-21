package com.koliving.api.room.domain;

import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.room.exception.IllegalMoneyException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.math.BigInteger;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Money {

    @Transient
    public static final Long MIN_AMOUNT = 0L;

    @Column(nullable = false)
    private BigInteger amount;

    private Money(long amount) {
        validate(amount);
        this.amount = BigInteger.valueOf(amount);
    }

    public static Money valueOf(long amount) {
        return new Money(amount);
    }

    private void validate(long amount) {
        if (amount < MIN_AMOUNT) {
            throw new IllegalMoneyException(amount);
        }
    }

    public BigInteger value() {
        return this.amount;
    }

    public boolean isEmpty() {
        return this.amount.equals(BigInteger.ZERO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Money money = (Money) o;

        return Objects.equals(getAmount(), money.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAmount());
    }
}
