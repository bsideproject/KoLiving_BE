package com.koliving.api.room.domain.info;

import java.util.EnumSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Quantity {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FOUR_OR_OVER("4+"),
    FIVE("5"),
    SIX("6"),
    SIX_OR_OVER("6+");

    public static EnumSet<Quantity> defaultBathrooms() {
        return EnumSet.of(ONE, TWO, THREE, FOUR_OR_OVER);
    }

    public static EnumSet<Quantity> defaultRoommates() {
        return EnumSet.of(ONE, TWO, THREE, FOUR, FIVE, SIX_OR_OVER);
    }

    private final String desc;

}
