package com.koliving.api.location.application.dto;

public record LocationUpdateRequest(String name) {

    public static LocationUpdateRequest valueOf(String name) {
        return new LocationUpdateRequest(name);
    }
}
