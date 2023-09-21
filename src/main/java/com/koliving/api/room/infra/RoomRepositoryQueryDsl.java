package com.koliving.api.room.infra;

import com.koliving.api.room.application.dto.RoomSearchCondition;
import com.koliving.api.room.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryQueryDsl {
    Page<Room> search(Pageable pageable, RoomSearchCondition condition);
}
