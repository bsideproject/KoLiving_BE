package com.koliving.api.location.infra;

import com.koliving.api.BaseDataJpaTest;
import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지역 리파지토리 테스트")
class LocationRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("상위 지역을 생성할 수 있다")
    public void createUpperLocation() {
        // given
        final Location location = Location.valueOf("seongdong", LocationType.GU);

        // when
        locationRepository.save(location);

        // then
        assertThat(location.getId()).isNotNull();
        assertThat(location.getUpperLocation()).isNull();
    }

    @Test
    @DisplayName("하위 지역을 생성할 수 있다")
    public void createLowerLocation() {
        // given
        final Location upperLocation = locationRepository.save(
            Location.valueOf("seongdong", LocationType.GU));

        // when
        final Location actual = locationRepository.save(Location.valueOf("songjeong", LocationType.DONG, upperLocation));

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUpperLocationId()).isNotNull();
    }

    @Test
    @DisplayName("이름으로 조회할 수 있다")
    public void findByName() {
        // given
        String name = "seongdong";

        locationRepository.save(
            Location.valueOf(name, LocationType.GU));

        // when
        Optional<Location> actual = locationRepository.findByName(name);
        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get().getName().equals(name));
    }
}