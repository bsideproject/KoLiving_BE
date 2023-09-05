package com.koliving.api.room.infra;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;
import com.koliving.api.location.infra.LocationRepository;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.FurnishingType;
import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.info.Quantity;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.koliving.api.fixtures.LocationFixture.성동구;
import static com.koliving.api.fixtures.MaintenanceFixture.관리비_없음;
import static com.koliving.api.fixtures.RoomInfoFixture.스튜디오_방1_욕실1_룸메1;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("룸 리파지토리 테스트")
@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FurnishingRepository furnishingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        final List<Furnishing> furnishings = Arrays.stream(FurnishingType.values())
            .map(Furnishing::valueOf)
            .collect(Collectors.toList());

        furnishingRepository.saveAll(furnishings);
    }

    @Test
    @DisplayName("룸 객체 생성하기")
    void create() {
        // given
        final Location location = locationRepository.save(
            Location.valueOf(
                "seongsu",
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
                관리비_없음,
                Sets.newHashSet(),
                LocalDate.of(2023, 8, 29),
                "설명이에요"
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
        assertThat(actual.getFurnishings()).hasSize(0);
        assertThat(actual.getAvailableDate()).isEqualTo(LocalDate.of(2023, 8, 29));
        assertThat(actual.getDescription()).isEqualTo("설명이에요");
    }

    @Test
    @DisplayName("가구가 있는 방 생성하기")
    void createWithFurnishings() {
        // given
        final Location location = locationRepository.save(
            Location.valueOf(
                "seongsu",
                LocationType.DONG,
                locationRepository.save(성동구)
            )
        );

        final Furnishing tv = furnishingRepository.findByType(FurnishingType.TV)
            .orElseThrow(NoSuchElementException::new);

        final Furnishing airConditioner = furnishingRepository.findByType(FurnishingType.AIR_CONDITIONER)
            .orElseThrow(NoSuchElementException::new);

        // when
        Room savedRoom = roomRepository.save(
            Room.valueOf(
                location,
                스튜디오_방1_욕실1_룸메1,
                Money.empty(),
                Money.empty(),
                관리비_없음,
                Sets.newLinkedHashSet(tv, airConditioner),
                LocalDate.of(2023, 8, 29),
                "설명이에요"
            )
        );

        roomRepository.flush();

        Room actual = roomRepository.findById(savedRoom.getId())
            .orElseThrow(NoSuchElementException::new);

        assertThat(actual.getDeposit().value()).isEqualTo(0);
        assertThat(actual.getMonthlyRent().value()).isEqualTo(0);
        assertThat(actual.getMaintenance().value()).isEqualTo(0);
        assertThat(actual.getRoomInfo().getRoomType().isStudio()).isTrue();
        assertThat(actual.getRoomInfo().getBedrooms()).isEqualTo(Quantity.ONE);
        assertThat(actual.getRoomInfo().getBathrooms()).isEqualTo(Quantity.ONE);
        assertThat(actual.getRoomInfo().getRoommates()).isEqualTo(Quantity.ONE);
        assertThat(actual.getFurnishings()).hasSize(2);
        assertThat(actual.getAvailableDate()).isEqualTo(LocalDate.of(2023, 8, 29));
        assertThat(actual.getDescription()).isEqualTo("설명이에요");
    }
}
