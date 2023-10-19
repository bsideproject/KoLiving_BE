package com.koliving.api.room.infra;

import com.koliving.api.room.application.dto.RoomResponse;
import com.koliving.api.room.application.dto.RoomSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryQueryDsl {

    Page<RoomResponse> search(Pageable pageable, RoomSearchCondition condition);

    Page<RoomResponse> likedRooms(Pageable pageable, Long userId);
}
