package com.koliving.api.room.infra;

import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.TypeOfHousing;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("룸 리파지토리 테스트")
@DataJpaTest
class RoomRepositoryTest {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TypeOfHousingRepository typeOfHousingRepository;

    @BeforeEach
    void setUp() {
        typeOfHousingRepository.saveAll(
            Lists.newArrayList(
                TypeOfHousing.valueOf(RoomType.STUDIO),
                TypeOfHousing.valueOf(RoomType.ONE_BED_FLATS),
                TypeOfHousing.valueOf(RoomType.SHARE_HOUSE)
            )
        );
    }

    @Test
    @DisplayName("룸 객체 생성하기")
    void create() {
        // given
        TypeOfHousing studio = typeOfHousingRepository.findByRoomType(RoomType.STUDIO);
        TypeOfHousing shareHouse = typeOfHousingRepository.findByRoomType(RoomType.SHARE_HOUSE);

        // when
        Room savedRoom = roomRepository.save(new Room(1L, Money.valueOf(0), Money.valueOf(0),
            Lists.newArrayList(studio, shareHouse)));

        Room actual = roomRepository.findOneWithRoomTypesById(savedRoom.getId())
            .orElseThrow(NoSuchElementException::new);

        assertThat(actual.getDeposit()).isEqualTo(Money.valueOf(0));
        assertThat(actual.getMonthlyRent()).isEqualTo(Money.valueOf(0));
        assertThat(actual.getTypes()).hasSize(2);
    }
}
