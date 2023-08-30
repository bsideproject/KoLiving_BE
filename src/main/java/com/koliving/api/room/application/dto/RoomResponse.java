package com.koliving.api.room.application.dto;

import com.koliving.api.location.application.dto.LocationResponse;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.Maintenance;
import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.info.RoomInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : haedoang date : 2023/08/26 description :
 */
@Schema(description = "방 조회 정보")
public record RoomResponse(

    @Schema(description = "방 고유 키")
    Long id,

    @Schema(description = "지역 정보")
    LocationResponse location,

    @Schema(description = "보증금 정보")
    Money deposit,

    @Schema(description = "월세 정보")
    Money monthlyRent,

    @Schema(description = "관리비 정보")
    Maintenance maintenance,

    @Schema(description = "방 상세 정보")
    RoomInfo roomInfo,

    @Schema(description = "방 가구 정보")
    Set<Furnishing> furnishings,

    @Schema(description = "방문 가능 일자")
    LocalDate availableDate,

    @Schema(description = "방 설명 정보")
    String description,

    @Schema(description = "유저 정보")
    MockUser mockUser,

    @Schema(description = "방 이미지 정보")
    List<MockImage> images

) {

    public static RoomResponse valueOf(Room entity) {
        return new RoomResponse(
            entity.getId(),
            LocationResponse.valueOf(entity.getLocation()),
            entity.getDeposit(),
            entity.getMonthlyRent(),
            entity.getMaintenance(),
            entity.getRoomInfo(),
            entity.getFurnishings(),
            entity.getAvailableDate(),
            entity.getDescription(),
            new MockUser(),
            List.of(
                new MockImage(1L, "https://t1.daumcdn.net/cfile/tistory/1748214B5084D30720"),
                new MockImage(2L, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_bK96glRFn6xYetjiVvcsGpH8KimDQXPulA&usqp=CAU"),
                new MockImage(3L, "https://cdn-icons-png.flaticon.com/512/3135/3135823.png")
            )
        );
    }

    @Getter
    public static class MockUser {
        private final String firstName = "NAMI";
        private final String lastName = "OH";
        private final String profileImage = "https://cdn-icons-png.flaticon.com/512/3135/3135823.png";
        private final int age = 30;
        private final Gender gender = Gender.FEMALE;
    }

    public enum Gender {
        MALE, FEMALE
    }

    @Getter
    @AllArgsConstructor
    public static class MockImage {
        private Long id;
        private String url;
    }
}
