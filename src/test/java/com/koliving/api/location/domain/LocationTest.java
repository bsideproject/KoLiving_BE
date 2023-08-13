package com.koliving.api.location.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지역 도메인 테스트")
class LocationTest {

    @Test
    @DisplayName("지역 도메인 생성")
    public void create() {
        // given
        final Location actual = Location.valueOf("seongdong", "성동", LocationType.GU);

        // then
        assertThat(actual.displayName()).isEqualTo("seongdong-gu");
    }

    @Test
    @DisplayName("지역이 상위가 아닌경우 upperLocation은 필수이다")
    public void requiredUpperLocation() {
        // given
        assertThat(LocationType.DONG.isTopLocation()).isFalse();

        // when & then
        assertThatThrownBy(
            () -> Location.valueOf("jayang3", "자양3", LocationType.DONG)
        ).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ServiceError.INVALID_LOCATION.getMessage());
    }

    @Test
    @DisplayName("지역이 최상위인 경우 upperLocation을 가질 수 없다")
    public void notRequiredUpperLocation() {
        // given
        final Location upperLocation = Location.valueOf("gwangjin", "광진", LocationType.GU);
        assertThat(LocationType.GU.isTopLocation()).isTrue();

        // when & then
        assertThatThrownBy(
            () -> Location.valueOf("seongdong", "성동", LocationType.GU, upperLocation)
        ).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ServiceError.INVALID_LOCATION.getMessage());
    }
}