package com.koliving.api.room.domain;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import autoparams.AutoSource;
import autoparams.Repeat;
import autoparams.ValueAutoSource;
import autoparams.customization.Fix;
import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("관리비 도메인 테스트")
class MaintenanceTest {

    @Test
    @DisplayName("관리비 0원인 객체 생성하기")
    void createEmpty() {
        // when
        final Maintenance actual = Maintenance.empty();

        // then
        assertThat(actual.value()).isEqualTo(0);
        assertThat(actual.isGasIncluded()).isFalse();
        assertThat(actual.isWaterIncluded()).isFalse();
        assertThat(actual.isElectricityIncluded()).isFalse();
        assertThat(actual.isCleaningIncluded()).isFalse();
    }

    @ParameterizedTest
    @ValueAutoSource(ints = { 0 })
    @DisplayName("관리비가 0원인 경우 옵션은 불포함이어야 한다")
    void createFailByIncluded(int candidate) {
        // then
        Assertions.assertAll(
            () -> assertThatThrownBy(() -> Maintenance.valueOf(Money.valueOf(candidate), TRUE, FALSE, FALSE, FALSE))
                .isInstanceOf(KolivingServiceException.class)
                .hasMessageContaining(ServiceError.ILLEGAL_MAINTENANCE.getMessage()),
            () -> assertThatThrownBy(() -> Maintenance.valueOf(Money.valueOf(candidate), FALSE, TRUE, FALSE, FALSE))
                .isInstanceOf(KolivingServiceException.class)
                .hasMessageContaining(ServiceError.ILLEGAL_MAINTENANCE.getMessage()),
            () -> assertThatThrownBy(() -> Maintenance.valueOf(Money.valueOf(candidate), FALSE, FALSE, TRUE, FALSE)).isInstanceOf(KolivingServiceException.class)
                .hasMessageContaining(ServiceError.ILLEGAL_MAINTENANCE.getMessage()),
            () -> assertThatThrownBy(() -> Maintenance.valueOf(Money.valueOf(candidate), FALSE, FALSE, FALSE, TRUE)).isInstanceOf(KolivingServiceException.class)
                .hasMessageContaining(ServiceError.ILLEGAL_MAINTENANCE.getMessage())
        );
    }

    @ParameterizedTest
    @AutoSource
    @Repeat(10)
    @DisplayName("관리비는 500만원보다 작아야 한다")
    void createFailByGreaterThanMaxAmount(@Min(5_000_001) int candidate1, boolean candidate2) {
        // then
        assertThatThrownBy(() -> Maintenance.valueOf(Money.valueOf(candidate1), candidate2, candidate2, candidate2, candidate2))
            .isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ServiceError.INVALID_MAINTENANCE_FEE.getMessage());
    }

    @Test
    @DisplayName("관리비가 null인 경우 0원이다")
    void createEmptyAmount() {
        // when
        final Maintenance actual = Maintenance.valueOf(Money.valueOf(null), FALSE, FALSE, FALSE, FALSE);

        // then
        assertThat(actual.getMaintenanceFee()).isEqualTo(Money.valueOf(0));
        assertThat(actual.isGasIncluded()).isFalse();
        assertThat(actual.isWaterIncluded()).isFalse();
        assertThat(actual.isElectricityIncluded()).isFalse();
        assertThat(actual.isCleaningIncluded()).isFalse();
    }

}