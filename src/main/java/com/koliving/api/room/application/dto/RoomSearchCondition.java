package com.koliving.api.room.application.dto;

import com.koliving.api.room.domain.FurnishingType;
import com.koliving.api.room.domain.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Schema(description = "방 검색 조건")
public record RoomSearchCondition(
    @Schema(description = "지역 고유 키 리스트")
    List<Long> locationIds,

    @Schema(description = "보증금 최소 금액")
    Integer minDeposit,

    @Schema(description = "보증금 최대 금액")
    Integer maxDeposit,

    @Schema(description = "월세 최소 금액")
    Integer minMonthlyRent,

    @Schema(description = "월세 최대 금액")
    Integer maxMonthlyRent,

    @Schema(description = "입주 가능 일자", example = "2023-09-01")
    LocalDate availableDate,

    @Schema(description = "룸 타입", example = "STUDIO, ONE_BED_FLATS")
    List<RoomType> types,

    @Schema(description = "가구류", example = "TV, DOOR_LOCK")
    List<FurnishingType> furnishingTypes
    ) {
}
