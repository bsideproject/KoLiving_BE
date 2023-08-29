package com.koliving.api.room.infra;

import static com.koliving.api.fixtures.LocationFixture.성동구;
import static com.koliving.api.fixtures.MaintenanceFixture.관리비_없음;
import static com.koliving.api.fixtures.RoomInfoFixture.스튜디오_방1_욕실1_룸메1;
import static org.assertj.core.api.Assertions.assertThat;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;
import com.koliving.api.location.infra.LocationRepository;
import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.info.Quantity;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("룸 리파지토리 테스트")
@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("룸 객체 생성하기")
    void create() {
        // given
        final Location location = locationRepository.save(
            Location.valueOf(
                "seongsu",
                "성수",
                LocationType.DONG,
                locationRepository.save(성동구)
            )
        );

        // when
        Room savedRoom = roomRepository.save(
            Room.valueOf(
                location,
                스튜디오_방1_욕실1_룸메1,
                Money.empty(),
                Money.empty(),
                관리비_없음
            )
        );

        Room actual = roomRepository.findById(savedRoom.getId())
            .orElseThrow(NoSuchElementException::new);

        assertThat(actual.getDeposit().value()).isEqualTo(0);
        assertThat(actual.getMonthlyRent().value()).isEqualTo(0);
        assertThat(actual.getMaintenance().value()).isEqualTo(0);
        assertThat(actual.getRoomInfo().getRoomType().isStudio()).isTrue();
        assertThat(actual.getRoomInfo().getBedrooms()).isEqualTo(Quantity.ONE);
        assertThat(actual.getRoomInfo().getBathrooms()).isEqualTo(Quantity.ONE);
        assertThat(actual.getRoomInfo().getRoommates()).isEqualTo(Quantity.ONE);
    }
}
