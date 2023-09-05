package com.koliving.api.location.application.dto;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;

public record LocationSaveRequest(String name, LocationType locationType, Long upperLocationId) {

    public Location toEntity(Location upperLocation) {
        return Location.valueOf(name, locationType, upperLocation);
    }
}
