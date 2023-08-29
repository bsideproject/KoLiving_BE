package com.koliving.api.room.application.dto;


import com.koliving.api.room.domain.Furnishing;

public record FurnishingResponse(Long id, String desc) {

    public static FurnishingResponse valueOf(Furnishing entity) {
        return new FurnishingResponse(
            entity.getId(),
            entity.getType().getDescription()
        );
    }
}
