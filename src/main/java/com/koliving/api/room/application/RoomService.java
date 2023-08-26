package com.koliving.api.room.application;

import com.koliving.api.room.application.dto.RoomResponse;
import com.koliving.api.room.infra.RoomRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author : haedoang date : 2023/08/26 description :
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponse> list() {
        return roomRepository.findAll()
            .stream()
            .map(RoomResponse::valueOf)
            .collect(Collectors.toList());
    }
}
