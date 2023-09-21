package com.koliving.api.room.application;

import static com.koliving.api.base.ServiceError.RECORD_NOT_EXIST;

import com.google.common.collect.Sets;
import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.file.infra.ImageFileRepository;
import com.koliving.api.location.domain.Location;
import com.koliving.api.location.infra.LocationRepository;
import com.koliving.api.room.application.dto.RoomResponse;
import com.koliving.api.room.application.dto.RoomSaveRequest;
import com.koliving.api.room.application.dto.RoomSearchCondition;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.QRoom;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.infra.FurnishingRepository;
import com.koliving.api.room.infra.RoomRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * author : haedoang date : 2023/08/26 description :
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

    private final FurnishingRepository furnishingRepository;
    private final LocationRepository locationRepository;
    private final RoomRepository roomRepository;
    private final ImageFileRepository imageFileRepository;

    public List<RoomResponse> list() {
        return roomRepository.findAll()
            .stream()
            .map(RoomResponse::valueOf)
            .collect(Collectors.toList());
    }

    @Transactional
    public Long save(RoomSaveRequest request) {
        Room room = request.toEntity(
            getLocationById(request.locationId()),
            getFurnishingsByIds(request.furnishingIds()),
            getImageFiles(request.imageIds())
        );
        final Room savedRoom = roomRepository.save(room);

        return savedRoom.getId();
    }

    private Set<ImageFile> getImageFiles(Set<Long> imageIds) {
        List<ImageFile> images = imageFileRepository.findAllById(imageIds);

        if (images.size() != imageIds.size()) {
            throw new IllegalArgumentException();
        }
        return Sets.newHashSet(images);
    }

    private Set<Furnishing> getFurnishingsByIds(Set<Long> furnishingIds) {
        if (CollectionUtils.isEmpty(furnishingIds)) {
            return Collections.emptySet();
        }

        final List<Furnishing> furnishings = furnishingRepository.findAllById(furnishingIds);

        if (furnishings.size() != furnishingIds.size()) {
            throw new KolivingServiceException(ServiceError.RECORD_NOT_EXIST);
        }

        return Sets.newHashSet(furnishings);
    }

    private Location getLocationById(Long locationId) {
        final Location location = locationRepository.findById(locationId)
            .orElseThrow(() -> new KolivingServiceException(RECORD_NOT_EXIST));

        if (location.getLocationType().isTopLocation()) {
            throw new KolivingServiceException(ServiceError.INVALID_LOCATION);
        }

        return location;
    }

    public Page<Room> search(Pageable pageable, RoomSearchCondition condition) {
        return roomRepository.search(pageable, condition);
    }
}
