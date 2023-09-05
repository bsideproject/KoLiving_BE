package com.koliving.api.location.application;

import com.koliving.api.location.application.dto.LocationResponse;
import com.koliving.api.location.application.dto.LocationSaveRequest;
import com.koliving.api.location.application.dto.LocationUpdateRequest;
import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;
import com.koliving.api.location.infra.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.koliving.api.fixtures.LocationFixture.성동구;
import static com.koliving.api.fixtures.LocationFixture.성수동;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("지역 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    @Mock
    private Location location;

    @Test
    @DisplayName("지역을 등록할 수 있다")
    public void create() {
        // given
        final LocationSaveRequest given =
            new LocationSaveRequest("seongdong", LocationType.GU, null);
        when(locationRepository.save(any())).thenReturn(location);
        when(location.getId()).thenReturn(1L);

        // when
        final Long actual = locationService.save(given);

        // then
        assertThat(actual).isEqualTo(1L);
    }

    @Test
    @DisplayName("지역을 조회할 수 있다")
    public void search() {
        // given
        when(locationRepository.findAll()).thenReturn(List.of(성동구, 성수동));

        // when
        final List<LocationResponse> actual = locationService.findAll();

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("지역명을 수정할 수 있다")
    public void update() {
        // given
        final Location given = Location.valueOf("seongdong", LocationType.GU, null);
        when(locationRepository.findById(any()))
            .thenReturn(Optional.of(given));

        // then
        locationService.update(1L, LocationUpdateRequest.valueOf("gwangjin"));

        // then
        assertThat(given.getName()).isEqualTo("gwangjin");
    }

    @Test
    @DisplayName("지역을 삭제할 수 있다")
    public void delete() {
        // when
        locationService.deleteById(1L);

        // then
        verify(locationRepository).deleteById(1L);
    }
}