package com.koliving.api.location.application;

import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.location.application.dto.LocationResponse;
import com.koliving.api.location.application.dto.LocationSaveRequest;
import com.koliving.api.location.application.dto.LocationUpdateRequest;
import com.koliving.api.location.domain.Location;
import com.koliving.api.location.infra.LocationRepository;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.koliving.api.base.ServiceError.RECORD_NOT_EXIST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public Long save(LocationSaveRequest request) {
        final Location savedLocation = locationRepository.save(
            request.toEntity(getUpperLocation(request))
        );
        return savedLocation.getId();
    }

    private Location getUpperLocation(LocationSaveRequest request) {
        if (Objects.isNull(request.upperLocationId())) {
            return null;
        }
        return findById(request.upperLocationId());
    }

    public LocationResponse findOne(Long id) {
        final Location location = findById(id);
        return LocationResponse.valueOf(location);
    }

    public List<LocationResponse> findAll() {
        return locationRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Location::getName))
            .map(LocationResponse::valueOf)
            .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, LocationUpdateRequest request) {
        final Location location = findById(id);
        location.update(request.name());
    }

    @Transactional
    public void deleteById(Long id) {
        locationRepository.deleteById(id);
    }

    private Location findById(Long id) {
        return locationRepository.findById(id)
            .orElseThrow(() -> new KolivingServiceException(RECORD_NOT_EXIST));
    }
}
