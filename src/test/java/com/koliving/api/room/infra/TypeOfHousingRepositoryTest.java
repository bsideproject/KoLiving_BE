package com.koliving.api.room.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.TypeOfHousing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("룸 타입 리포지토리 테스트")
class TypeOfHousingRepositoryTest {

    @Autowired
    private TypeOfHousingRepository typeOfHousingRepository;

    @Test
    @DisplayName("스튜디오 타입 생성하기")
    public void createStudioTest() {
        // given
        final TypeOfHousing given = TypeOfHousing.valueOf(RoomType.STUDIO);

        // when
        final TypeOfHousing actual = typeOfHousingRepository.save(given);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getType().isStudio()).isTrue();
    }

    @Test
    @DisplayName("원룸 타입 생성하기")
    public void create1BedFlatTest() {
        // given
        final TypeOfHousing given = TypeOfHousing.valueOf(RoomType.ONE_BED_FLATS);

        // when
        final TypeOfHousing actual = typeOfHousingRepository.save(given);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getType().isOneBedFlats()).isTrue();
    }

    @Test
    @DisplayName("쉐어하우스 타입 생성하기")
    public void createShareHouseTest() {
        // given
        final TypeOfHousing given = TypeOfHousing.valueOf(RoomType.SHARE_HOUSE);

        // when
        final TypeOfHousing actual = typeOfHousingRepository.save(given);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getType().isShareHouse()).isTrue();
    }
}
