package com.koliving.api.room.infra;

import com.koliving.api.BaseDataJpaTest;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.FurnishingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("가구 리포지토리 테스트")
class FurnishingRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private FurnishingRepository furnishingRepository;

    @Test
    @DisplayName("TV 타입 생성하기")
    public void createTVTypeTest() {
        // given
        final Furnishing given = Furnishing.valueOf(FurnishingType.TV);

        // when
        final Furnishing actual = furnishingRepository.save(given);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getType()).isEqualTo(FurnishingType.TV);
    }

    @Test
    @DisplayName("타입으로 조회하기")
    public void findByType() {
        // given
        furnishingRepository.save(Furnishing.valueOf(FurnishingType.TV));

        // when
        final Furnishing actual = furnishingRepository.findByType(FurnishingType.TV)
            .orElseThrow(NoSuchElementException::new);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getType()).isEqualTo(FurnishingType.TV);
    }
}
