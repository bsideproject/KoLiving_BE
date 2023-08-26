package com.koliving.api.room.domain;


import com.koliving.api.room.domain.info.Quantity;
import java.util.EnumSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    STUDIO(
        "studio",
        EnumSet.of(Quantity.ONE),
        Quantity.defaultBathrooms(),
        Quantity.defaultRoommates()
    ),
    ONE_BED_FLATS(
        "1bed flats",
        EnumSet.of(Quantity.ONE),
        Quantity.defaultBathrooms(),
        Quantity.defaultRoommates()
    ),
    SHARE_HOUSE(
        "share house",
        EnumSet.of(Quantity.TWO, Quantity.THREE, Quantity.FOUR, Quantity.FIVE, Quantity.SIX_OR_OVER),
        Quantity.defaultBathrooms(), Quantity.defaultRoommates()
    );

    private final String description;
    private final EnumSet<Quantity> bedrooms;
    private final EnumSet<Quantity> bathrooms;
    private final EnumSet<Quantity> roommates;

    public boolean isStudio() {
        return this == STUDIO;
    }

    public boolean isOneBedFlats() {
        return this == ONE_BED_FLATS;
    }

    public boolean isShareHouse() {
        return this == SHARE_HOUSE;
    }
}
