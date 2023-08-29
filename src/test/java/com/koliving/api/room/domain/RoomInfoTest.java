package com.koliving.api.room.domain;

import static com.koliving.api.base.ServiceError.ILLEGAL_ROOM_INFO;
import static com.koliving.api.room.domain.info.Quantity.ONE;
import static com.koliving.api.room.domain.info.Quantity.THREE;
import static com.koliving.api.room.domain.info.Quantity.TWO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.room.domain.info.Quantity;
import com.koliving.api.room.domain.info.RoomInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * author : haedoang date : 2023/08/29 description :
 */
@DisplayName("RoomInfo 도메인 테스트")
public class RoomInfoTest {

    @Test
    @DisplayName("스튜디오 객체 생성하기")
    void createStudio() {
        // when
        final RoomInfo roomInfo = RoomInfo.valueOf(RoomType.STUDIO, ONE, TWO, THREE);

        // then
        assertThat(roomInfo.getRoomType().isStudio()).isTrue();
        assertThat(roomInfo.getBedrooms()).isEqualTo(ONE);
        assertThat(roomInfo.getBathrooms()).isEqualTo(TWO);
        assertThat(roomInfo.getRoommates()).isEqualTo(THREE);
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"TWO", "THREE", "FOUR", "FOUR_OR_OVER", "FIVE", "SIX", "SIX_OR_OVER"})
    @DisplayName("스튜디오는 침실이 1개여야 한다")
    void createFailStudioByBedrooms(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.STUDIO, candidate, ONE, ONE);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"FOUR", "FIVE", "SIX", "SIX_OR_OVER"})
    @DisplayName("스튜디오는 욕실은 1,2,3,4+ 중 하나여야 한다")
    void createFailStudioByBathrooms(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.STUDIO, ONE, candidate, ONE);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"FOUR_OR_OVER", "SIX"})
    @DisplayName("스튜디오는 룸메이트는 1,2,3,4,5,6+ 중 하나여야 한다")
    void createFailStudioByRoommates(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.STUDIO, ONE, ONE, candidate);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @Test
    @DisplayName("원룸 객체 생성하기")
    void createOneRoom() {
        // when
        final RoomInfo roomInfo = RoomInfo.valueOf(RoomType.ONE_BED_FLATS, ONE, TWO, THREE);

        // then
        assertThat(roomInfo.getRoomType().isOneBedFlats()).isTrue();
        assertThat(roomInfo.getBedrooms()).isEqualTo(ONE);
        assertThat(roomInfo.getBathrooms()).isEqualTo(TWO);
        assertThat(roomInfo.getRoommates()).isEqualTo(THREE);
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"TWO", "THREE", "FOUR", "FOUR_OR_OVER", "FIVE", "SIX", "SIX_OR_OVER"})
    @DisplayName("원룸는 침실 개수는 2,3,4,5,6")
    void createFailOneRoomByBedrooms(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.ONE_BED_FLATS, candidate, ONE, ONE);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"FOUR", "FIVE", "SIX", "SIX_OR_OVER"})
    @DisplayName("원룸은 욕실은 1,2,3,4+ 중 하나여야 한다")
    void createFailOneRoomByBathrooms(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.ONE_BED_FLATS, ONE, candidate, ONE);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"FOUR_OR_OVER", "SIX"})
    @DisplayName("원룸은 룸메이트는 1,2,3,4,5,6+ 중 하나여야 한다")
    void createFailOneRoomByRoommates(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.ONE_BED_FLATS, ONE, ONE, candidate);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @Test
    @DisplayName("쉐어하우스 객체 생성하기")
    void createShareHouse() {
        // when
        final RoomInfo roomInfo = RoomInfo.valueOf(RoomType.SHARE_HOUSE, TWO, TWO, THREE);

        // then
        assertThat(roomInfo.getRoomType().isShareHouse()).isTrue();
        assertThat(roomInfo.getBedrooms()).isEqualTo(TWO);
        assertThat(roomInfo.getBathrooms()).isEqualTo(TWO);
        assertThat(roomInfo.getRoommates()).isEqualTo(THREE);
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"ONE", "FOUR_OR_OVER", "SIX"})
    @DisplayName("쉐어하우스는 침실이 2,3,4,5,6+ 중 하나여야 한다")
    void createFailShareHouseByBedrooms(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.SHARE_HOUSE, candidate, ONE, ONE);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"FOUR", "FIVE", "SIX", "SIX_OR_OVER"})
    @DisplayName("쉐어하우스는 욕실은 1,2,3,4+ 중 하나여야 한다")
    void createFailShareHouseByBathrooms(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.SHARE_HOUSE, ONE, candidate, ONE);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
        value = Quantity.class,
        names = {"FOUR_OR_OVER", "SIX"})
    @DisplayName("쉐어하우스는 룸메이트는 1,2,3,4,5,6+ 중 하나여야 한다")
    void createFailShareHouseByRoommates(Quantity candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.SHARE_HOUSE, ONE, ONE, candidate);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }
}
