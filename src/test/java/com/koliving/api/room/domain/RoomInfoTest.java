package com.koliving.api.room.domain;

import static com.koliving.api.base.ServiceError.ILLEGAL_ROOM_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.room.domain.info.RoomInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * author : haedoang date : 2023/08/29 description :
 */
@DisplayName("RoomInfo 도메인 테스트")
public class RoomInfoTest {

    @Test
    @DisplayName("스튜디오 객체 생성하기")
    void createStudio() {
        // when
        final RoomInfo roomInfo = RoomInfo.valueOf(RoomType.STUDIO, 0, 2, 3);

        // then
        assertThat(roomInfo.getRoomType().isStudio()).isTrue();
        assertThat(roomInfo.getBedrooms()).isEqualTo(0);
        assertThat(roomInfo.getBathrooms()).isEqualTo(2);
        assertThat(roomInfo.getRoommates()).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(
        ints = {1, 2, 3, 4, 5, 6, 7, 8, 9}
    )
    @DisplayName("스튜디오는 침실이 0개여야 한다")
    void createFailStudioByBedrooms(Integer candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.STUDIO, candidate, 1, 1);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @Test
    @DisplayName("스튜디오는 욕실은 0보다 커야 한다")
    void createFailStudioByBathrooms() {
        // given
        int illegalBathRoomsSize = 0;

        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.STUDIO, 0, illegalBathRoomsSize, 1);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @ValueSource(
        ints = {1, 2, 3, 4, 5, 6, 7, 8}
    )
    @DisplayName("스튜디오는 룸메이트는 1이상이어야 한다")
    void createFailStudioByRoommates(Integer candidate) {
        // when
        final RoomInfo actual = RoomInfo.valueOf(RoomType.STUDIO, 0, 1, candidate);

        // then
        assertThat(actual.getRoommates()).isEqualTo(candidate);
    }

    @Test
    @DisplayName("원룸 객체 생성하기")
    void createOneRoom() {
        // when
        final RoomInfo roomInfo = RoomInfo.valueOf(RoomType.ONE_BED_FLATS, 1, 2, 3);

        // then
        assertThat(roomInfo.getRoomType().isOneBedFlats()).isTrue();
        assertThat(roomInfo.getBedrooms()).isEqualTo(1);
        assertThat(roomInfo.getBathrooms()).isEqualTo(2);
        assertThat(roomInfo.getRoommates()).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(
        ints = {2, 3, 4, 5, 6, 7, 8, 9}
    )
    @DisplayName("원룸는 침실 개수는 1개이어야 한다")
    void createFailOneRoomByBedrooms(Integer candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.ONE_BED_FLATS, candidate, 1, 1);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @ValueSource(
        ints = {1, 2, 3, 4, 5, 6, 7, 8}
    )
    @DisplayName("원룸은 욕실은 1이상이어야 한다")
    void createFailOneRoomByBathrooms(Integer candidate) {
        // when
        final RoomInfo actual = RoomInfo.valueOf(RoomType.ONE_BED_FLATS, 1, candidate, 1);

        // then
        assertThat(actual.getBathrooms()).isEqualTo(candidate);
    }

    @ParameterizedTest
    @ValueSource(
        ints = {1, 2, 3, 4, 5, 6, 7, 8}
    )
    @DisplayName("원룸은 룸메이트는 1,2,3,4,5,6+ 중 하나여야 한다")
    void createFailOneRoomByRoommates(Integer candidate) {
        // when
        final RoomInfo actual = RoomInfo.valueOf(RoomType.ONE_BED_FLATS, 1, 1, candidate);

        // then
        assertThat(actual.getRoommates()).isEqualTo(candidate);
    }

    @Test
    @DisplayName("쉐어하우스 객체 생성하기")
    void createShareHouse() {
        // when
        final RoomInfo roomInfo = RoomInfo.valueOf(RoomType.SHARE_HOUSE, 2, 2, 3);

        // then
        assertThat(roomInfo.getRoomType().isShareHouse()).isTrue();
        assertThat(roomInfo.getBedrooms()).isEqualTo(2);
        assertThat(roomInfo.getBathrooms()).isEqualTo(2);
        assertThat(roomInfo.getRoommates()).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(
        ints = {0, 1}
    )
    @DisplayName("쉐어하우스는 침실이 2이상 이어야 한다")
    void createFailShareHouseByBedrooms(Integer candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.SHARE_HOUSE, candidate, 1, 1);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @ValueSource(
        ints = {1, 2, 3, 4, 5, 6, 7, 8}
    )
    @DisplayName("쉐어하우스는 욕실은 1이상이어야 한다")
    void createFailShareHouseByBathrooms(Integer candidate) {
        // when & then
        assertThatThrownBy(() -> {
            RoomInfo.valueOf(RoomType.SHARE_HOUSE, 1, candidate, 1);
        }).isInstanceOf(KolivingServiceException.class)
            .hasMessageContaining(ILLEGAL_ROOM_INFO.getMessage());
    }

    @ParameterizedTest
    @ValueSource(
        ints = {1, 2, 3, 4, 5, 6, 7, 8}
    )
    @DisplayName("쉐어하우스는 룸메이트는 1이상이어야 한다")
    void createFailShareHouseByRoommates(Integer candidate) {
        // when
        final RoomInfo actual = RoomInfo.valueOf(RoomType.SHARE_HOUSE, 2, 1, candidate);

        // then
        assertThat(actual.getRoommates()).isEqualTo(candidate);
    }
}
