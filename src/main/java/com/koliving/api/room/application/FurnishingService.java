package com.koliving.api.room.application;

import com.koliving.api.room.application.dto.FurnishingResponse;
import com.koliving.api.room.infra.FurnishingRepository;
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

    public List<FurnishingResponse> list() {
        return furnishingRepository.findAll()
            .stream()
            .map(FurnishingResponse::valueOf)
            .collect(Collectors.toList());
    }
}
