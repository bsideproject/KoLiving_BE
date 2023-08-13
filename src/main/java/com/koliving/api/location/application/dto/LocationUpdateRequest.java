package com.koliving.api.location.application.dto;

public record LocationUpdateRequest(String enName, String krName) {

    public static LocationUpdateRequest valueOf(String enName, String krName) {
        return new LocationUpdateRequest(enName, krName);
    }
}
