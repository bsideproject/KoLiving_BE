package com.koliving.api.room.infra;

import com.koliving.api.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select r from TB_ROOM r join fetch r.types")
    List<Room> findAllWithRoomTypes();

    @Query("select r from TB_ROOM r join fetch r.types where r.id = :id")
    Optional<Room> findOneWithRoomTypesById(Long id);
}
