package com.koliving.api.room;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer>, RoomCustomRepository {
}
