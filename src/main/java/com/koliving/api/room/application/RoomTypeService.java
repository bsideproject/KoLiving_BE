package com.koliving.api.room.application;

import com.koliving.api.room.application.dto.RoomTypeResponse;
import com.koliving.api.room.application.dto.RoomTypeSaveRequest;
import com.koliving.api.room.infra.TypeOfHousingRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomTypeService {

    private final TypeOfHousingRepository typeOfHousingRepository;

    @Transactional
    public Long save(RoomTypeSaveRequest request) {
        return typeOfHousingRepository.save(request.toEntity()).getId();
    }

    public List<RoomTypeResponse> list() {
        return typeOfHousingRepository.findAll()
            .stream()
            .map(RoomTypeResponse::valueOf)
            .collect(Collectors.toList());
    }
}
