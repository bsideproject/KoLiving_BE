package com.koliving.api.room.application;

import static com.koliving.api.fixtures.TypeOfHousingFixture.쉐어하우스;
import static com.koliving.api.fixtures.TypeOfHousingFixture.스튜디오;
import static com.koliving.api.fixtures.TypeOfHousingFixture.원룸;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.koliving.api.room.application.dto.RoomTypeResponse;
import com.koliving.api.room.application.dto.RoomTypeSaveRequest;
import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.TypeOfHousing;
import com.koliving.api.room.infra.TypeOfHousingRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * author : haedoang date : 2023/08/21 description :
 */
@DisplayName("룸 타입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class RoomTypeServiceTest {

    @Mock
    private TypeOfHousingRepository typeOfHousingRepository;

    @InjectMocks
    private RoomTypeService roomTypeService;

    @Mock
    private TypeOfHousing typeOfHousing;

    @Test
    @DisplayName("룸타입을 등록할 수 있다")
    public void create() {
        // given
        final RoomTypeSaveRequest given =
            new RoomTypeSaveRequest(RoomType.STUDIO);
        when(typeOfHousingRepository.save(any())).thenReturn(typeOfHousing);
        when(typeOfHousing.getId()).thenReturn(1L);

        // when
        final Long actual = roomTypeService.save(given);

        // then
        assertThat(actual).isEqualTo(1L);
    }

    @Test
    @DisplayName("룸타입을 조회할 수 있다")
    public void search() {
        // given
        when(typeOfHousingRepository.findAll()).thenReturn(List.of(스튜디오, 원룸, 쉐어하우스));

        // when
        final List<RoomTypeResponse> actual = roomTypeService.list();

        // then
        assertThat(actual).hasSize(3);
    }
}