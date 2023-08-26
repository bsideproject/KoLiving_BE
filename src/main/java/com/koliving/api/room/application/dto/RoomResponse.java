package com.koliving.api.room.application.dto;

import com.koliving.api.room.domain.Maintenance;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.info.RoomInfo;

/**
 * author : haedoang date : 2023/08/26 description :
 */
public record RoomResponse(Long id, Long upLocationId, Long locationId, RoomInfo roomInfo, int monthlyRent, int deposit,
                           Maintenance maintenance) {

    public static RoomResponse valueOf(Room entity) {
        return new RoomResponse(
            entity.getId(),
            entity.getLocation().getUpperLocationId(),
            entity.getLocation().getId(),
            entity.getRoomInfo(),
            entity.getMonthlyRent().value(),
            entity.getDeposit().value(),
            entity.getMaintenance()
        );
    }
}
