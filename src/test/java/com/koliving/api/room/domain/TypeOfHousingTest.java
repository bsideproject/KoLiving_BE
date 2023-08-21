package com.koliving.api.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("룸 타입 테스트")
class TypeOfHousingTest {

    @Test
    @DisplayName("스튜디오 타입 생성하기")
    public void createStudioTest() {
        // when
        final TypeOfHousing actual = TypeOfHousing.valueOf(RoomType.STUDIO);

        // then
        assertThat(actual.getRoomType().isStudio()).isTrue();
    }

    @Test
    @DisplayName("원룸 타입 생성하기")
    public void createOneBedFlatsTest() {
        // when
        final TypeOfHousing actual = TypeOfHousing.valueOf(RoomType.ONE_BED_FLATS);

        // then
        assertThat(actual.getRoomType().isOneBedFlats()).isTrue();
    }

    @Test
    @DisplayName("쉐어하우스 타입 생성하기")
    public void createShareHouseTest() {
        // when
        final TypeOfHousing actual = TypeOfHousing.valueOf(RoomType.SHARE_HOUSE);

        // then
        assertThat(actual.getRoomType().isShareHouse()).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("RoomType에 null 값은 올 수 없다")
    public void createFailByRoomTypeIsNull(RoomType candidate) {
        // when & then
        assertThatThrownBy(() -> TypeOfHousing.valueOf(candidate))
            .isInstanceOf(NullPointerException.class);
    }
}