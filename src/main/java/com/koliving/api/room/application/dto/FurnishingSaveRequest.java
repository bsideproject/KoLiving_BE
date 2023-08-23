package com.koliving.api.room.application.dto;


import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.FurnishingType;
import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.TypeOfHousing;

public record FurnishingSaveRequest(FurnishingType furnishingType) {

    public Furnishing toEntity() {
        return Furnishing.valueOf(furnishingType);
    }
}
