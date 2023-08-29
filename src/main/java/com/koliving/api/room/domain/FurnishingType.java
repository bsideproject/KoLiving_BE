package com.koliving.api.room.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FurnishingType {
    BED("Bed"),
    INDUCTION("induction"),
    AIR_CONDITIONER("Air conditioner"),
    GAS_STOVE("Gas stove"),
    REFRIGERATOR("Refrigerator"),
    WARDROBE("Wardrobe"),
    WASHING_MACHINE("Washing machine"),
    DOOR_LOCK("Door lock"),
    TV("TV");

    private final String description;
}
