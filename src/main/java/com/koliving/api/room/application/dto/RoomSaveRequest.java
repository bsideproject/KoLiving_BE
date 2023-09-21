package com.koliving.api.room.application.dto;

import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.location.domain.Location;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.Maintenance;
import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.info.Quantity;
import com.koliving.api.room.domain.info.RoomInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Set;

/**
 * author : haedoang date : 2023/08/29 description :
 */
@Schema(description = "방 등록 정보")
public record RoomSaveRequest(
    @Schema(description = "지역 고유 키")
    Long locationId,

    @Schema(description = "방 구분 1.STUDIO 2.ONE_BED_FLATS 3.SHARE_HOUSE")
    RoomType roomType,

    @Schema(description = "침실 수 ONE, TWO, THREE, FOUR, FIVE, SIX_OR_OVER")
    Quantity bedrooms,

    @Schema(description = "욕실 수 ONE, TWO, THREE, FOUR_OR_OVER")
    Quantity bathrooms,

    @Schema(description = "룸메이트 수 ONE, TWO, THREE, FOUR, FIVE, SIX_OR_OVER")
    Quantity roommates,

    @Schema(description = "보증금(원)")
    Integer deposit,

    @Schema(description = "월세(원)")
    Integer monthlyRent,

    @Schema(description = "관리비(원)")
    Integer maintenanceFee,

    @Schema(description = "관리비 > 가스 포함여부")
    Boolean gasIncluded,

    @Schema(description = "관리비 > 수도세 포함여부")
    Boolean waterIncluded,

    @Schema(description = "관리비 > 전기세 포함여부")
    Boolean electricityIncluded,

    @Schema(description = "관리비 > 청소비 포함여부")
    Boolean cleaningIncluded,

    @Schema(description = "가구 고유키 리스트")
    Set<Long> furnishingIds,

    @Schema(description = "방문 가능일자")
    LocalDate availableDate,

    @Schema(description = "상세 설명")
    String description,

    @Schema(description = "이미지 고유키 리스트")
    Set<Long> imageIds
) {

    public Money toDeposit() {
        return Money.valueOf(deposit);
    }

    public Money toMonthlyRent() {
        return Money.valueOf(monthlyRent);
    }

    public Maintenance toMaintenance() {
        return Maintenance.valueOf(
            Money.valueOf(maintenanceFee),
            gasIncluded,
            waterIncluded,
            electricityIncluded,
            cleaningIncluded
        );
    }

    public RoomInfo toRoomInfo() {
        return RoomInfo.valueOf(
            roomType,
            bedrooms,
            bathrooms,
            roommates
        );
    }

    public Room toEntity(Location location, Set<Furnishing> furnishings, Set<ImageFile> imageFiles) {
        return Room.valueOf(
            location,
            toRoomInfo(),
            toDeposit(),
            toMonthlyRent(),
            toMaintenance(),
            furnishings,
            availableDate(),
            description(),
            imageFiles
        );
    }
}
