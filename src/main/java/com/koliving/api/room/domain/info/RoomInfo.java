package com.koliving.api.room.domain.info;


import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.room.domain.RoomType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RoomInfo {

    @Enumerated(STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Quantity bedrooms;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Quantity bathrooms;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Quantity roommates;

    private RoomInfo(
        RoomType roomType,
        Quantity bedrooms,
        Quantity bathrooms,
        Quantity roommates
    ) {
        validate(roomType, bedrooms);
        this.roomType = roomType;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.roommates = roommates;
    }

    private void validate(RoomType roomType, Quantity bedrooms) {
        if ((roomType.isStudio() || roomType.isOneBedFlats()) && bedrooms.isNotOne()) {
            throw new KolivingServiceException(ServiceError.ILLEGAL_ROOM_INFO);
        }
    }

    public static RoomInfo valueOf(RoomType roomType, Quantity bedrooms, Quantity bathrooms, Quantity roommates) {
        return new RoomInfo(roomType, bedrooms, bathrooms, roommates);
    }
}
