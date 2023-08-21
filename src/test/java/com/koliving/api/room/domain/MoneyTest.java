package com.koliving.api.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.koliving.api.room.exception.IllegalMoneyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("금액 불변 객체 테스트")
class MoneyTest {

    @Test
    @DisplayName("금액 객체 생성 테스트")
    public void create() {
        // given
        Money money = Money.valueOf(Money.MIN_AMOUNT);

        // then
        assertThat(money).isEqualTo(Money.valueOf(0));
    }

    @DisplayName("금액은 0보다 크거나 같아야 한다")
    @ParameterizedTest(name = "유효하지 않은 값: " + ParameterizedTest.ARGUMENTS_PLACEHOLDER)
    @ValueSource(ints = {Integer.MIN_VALUE, -10, -1})
    public void invalidItemNo(int candidate) {
        assertThatThrownBy(() -> Money.valueOf(candidate))
            .isInstanceOf(IllegalMoneyException.class)
            .hasMessageContaining(IllegalMoneyException.MESSAGE);
    }
}