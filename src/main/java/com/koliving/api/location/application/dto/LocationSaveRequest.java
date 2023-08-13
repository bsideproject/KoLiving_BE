package com.koliving.api.location.application.dto;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;

public record LocationSaveRequest(String enName, String krName, LocationType locationType, Long upperLocationId) {

    public Location toEntity(Location upperLocation) {
        return Location.valueOf(enName, krName, locationType, upperLocation);
    }
}
