package com.koliving.api.location.application.dto;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;


public record LocationResponse(Long id, LocationResponse upperLocation, String name, String displayName,
                               LocationType locationType) {

    public static LocationResponse valueOf(Location entity) {
        return new LocationResponse(
            entity.getId(),
            entity.getLocationType().isTopLocation() ? null : LocationResponse.valueOf(entity.getUpperLocation()),
            entity.getName(),
            entity.displayName(),
            entity.getLocationType()
        );
    }
}
