package com.koliving.api.room.application.dto;


import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.TypeOfHousing;

public record RoomTypeSaveRequest(RoomType roomType) {

    public TypeOfHousing toEntity() {
        return TypeOfHousing.valueOf(roomType);
    }
}
