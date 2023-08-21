package com.koliving.api.room.domain;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    STUDIO("studio"),
    ONE_BED_FLATS("1bed flats"),
    SHARE_HOUSE("share house");

    @JsonValue
    private final String description;

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
