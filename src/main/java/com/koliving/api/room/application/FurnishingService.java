package com.koliving.api.room.application;

import com.koliving.api.room.application.dto.FurnishingResponse;
import com.koliving.api.room.application.dto.FurnishingSaveRequest;
import com.koliving.api.room.application.dto.RoomTypeResponse;
import com.koliving.api.room.application.dto.RoomTypeSaveRequest;
import com.koliving.api.room.infra.FurnishingRepository;
import com.koliving.api.room.infra.TypeOfHousingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FurnishingService {

    private final FurnishingRepository furnishingRepository;

    @Transactional
    public Long save(FurnishingSaveRequest request) {
        return furnishingRepository.save(request.toEntity()).getId();
    }

    public List<FurnishingResponse> list() {
        return furnishingRepository.findAll()
            .stream()
            .map(FurnishingResponse::valueOf)
            .collect(Collectors.toList());
    }
}
