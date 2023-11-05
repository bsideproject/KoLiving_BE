package com.koliving.api.room.application.dto;


import com.koliving.api.room.domain.Furnishing;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가구 정보 조회")
public record FurnishingResponse(
    @Schema(description = "가구 고유 ID")
    Long id,

    @Schema(description = "가구 설명")
    String desc
) {

    public static FurnishingResponse valueOf(Furnishing entity) {
        return new FurnishingResponse(
            entity.getId(),
            entity.getType().getDescription()
        );
    }
}
