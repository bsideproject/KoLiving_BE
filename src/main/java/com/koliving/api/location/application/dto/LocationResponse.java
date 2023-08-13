package com.koliving.api.location.application.dto;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;


public record LocationResponse(Long id, Long upperLocationId, String enName, String krName, String displayName,
                               LocationType locationType) {

    public static LocationResponse valueOf(Location entity) {
        return new LocationResponse(
            entity.getId(),
            entity.getUpperLocationId(),
            entity.getEnName(),
            entity.getKrName(),
            entity.displayName(),
            entity.getLocationType()
        );
    }
}
