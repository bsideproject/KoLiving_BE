package com.koliving.api.location.application.dto;

import com.koliving.api.location.domain.Location;
import com.koliving.api.location.domain.LocationType;
import com.querydsl.core.annotations.QueryProjection;


public record LocationResponse(Long id, Long upperLocationId, String name, String displayName,
                               LocationType locationType) {

    @QueryProjection
    public LocationResponse {
    }

    public static LocationResponse valueOf(Location entity) {
        return new LocationResponse(
            entity.getId(),
            entity.getUpperLocationId(),
            entity.getName(),
            entity.displayName(),
            entity.getLocationType()
        );
    }
}
