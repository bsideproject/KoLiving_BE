package com.koliving.api.room.infra;

import com.koliving.api.fixtures.LocationFixture;
import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;
import com.koliving.api.location.infra.LocationRepository;
import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.RoomType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;

import static com.koliving.api.fixtures.LocationFixture.성동구;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("룸 리파지토리 테스트")
@DataJpaTest
class RoomRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoomRepository roomRepository;

//    @Test
//    @DisplayName("룸 객체 생성하기")
//    void create() {
//        // given
//        final Location location = locationRepository.save(
//            Location.valueOf("seongsu", "성수", LocationType.DONG, locationRepository.save(성동구)));
//
//        // when
//        Room savedRoom = roomRepository.save(new Room(location, Money.valueOf(0), Money.valueOf(0), RoomType.STUDIO));
//
//        Room actual = roomRepository.findById(savedRoom.getId())
//            .orElseThrow(NoSuchElementException::new);
//
//        assertThat(actual.getDeposit()).isEqualTo(Money.valueOf(0));
//        assertThat(actual.getMonthlyRent()).isEqualTo(Money.valueOf(0));
//        assertThat(actual.getRoomType().isStudio()).isTrue();
//    }
}
