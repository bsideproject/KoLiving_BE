package com.koliving.api.room.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FurnishingTest {

    @Test
    @DisplayName("TV 타입 생성하기")
    public void createTVTypeTest() {
        // when
        final Furnishing actual = Furnishing.valueOf(FurnishingType.TV);

        // then
        assertThat(actual.getType()).isEqualTo(FurnishingType.TV);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("FurnishingType은 null 값은 올 수 없다")
    public void createFailByFurnishingTypeIsNull(FurnishingType candidate) {
        // when & then
        assertThatThrownBy(() -> Furnishing.valueOf(candidate))
            .isInstanceOf(NullPointerException.class);
    }
}
