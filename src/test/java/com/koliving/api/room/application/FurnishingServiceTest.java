package com.koliving.api.room.application;

import com.koliving.api.room.application.dto.FurnishingResponse;
import com.koliving.api.room.application.dto.FurnishingSaveRequest;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.FurnishingType;
import com.koliving.api.room.infra.FurnishingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.koliving.api.fixtures.FurnishingFixture.도어락;
import static com.koliving.api.fixtures.FurnishingFixture.에어컨;
import static com.koliving.api.fixtures.FurnishingFixture.티비;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("가구 타입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FurnishingServiceTest {

    @Mock
    private FurnishingRepository furnishingRepository;

    @InjectMocks
    private FurnishingService furnishingService;

    @Mock
    private Furnishing furnishing;

    @Test
    @DisplayName("가구를 등록할 수 있다")
    public void create() {
        // given
        final FurnishingSaveRequest given =
            new FurnishingSaveRequest(FurnishingType.TV);
        when(furnishingRepository.save(any())).thenReturn(furnishing);
        when(furnishing.getId()).thenReturn(1L);

        // when
        final Long actual = furnishingService.save(given);

        // then
        assertThat(actual).isEqualTo(1L);
    }

    @Test
    @DisplayName("가구를 조회할 수 있다")
    public void search() {
        // given
        when(furnishingRepository.findAll()).thenReturn(List.of(도어락, 티비, 에어컨));

        // when
        final List<FurnishingResponse> actual = furnishingService.list();

        // then
        assertThat(actual).hasSize(3);
    }
}
