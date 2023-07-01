package com.koliving.api.room;

import com.koliving.api.furnishing.FurnishingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("roomService")
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;

    private final FurnishingRepository furnishingRepository;


    @Override
    @Transactional
    public void createRoom() {
        roomRepository.save();
        furnishingRepository.save();
    }

    @Override
    @Transactional
    public void getRoomList() {
        roomRepository.getRoomList();
    }
}
