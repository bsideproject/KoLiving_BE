package com.koliving.api.location.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationType {
    GU("gu", "구"),
    DONG("dong", "동");

    private final String enName;
    private final String krName;

    public boolean isRequiredUpperLocation() {
        return this == DONG;
    }

    public boolean isTopLocation() {
        return this == GU;
    }
}
