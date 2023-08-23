package com.koliving.api.room.application.dto;


import com.koliving.api.room.domain.TypeOfHousing;

public record RoomTypeResponse(Long id, String roomType) {

    public static RoomTypeResponse valueOf(TypeOfHousing entity) {
        return new RoomTypeResponse(
            entity.getId(),
            entity.getType().getDescription()
        );
    }
}
