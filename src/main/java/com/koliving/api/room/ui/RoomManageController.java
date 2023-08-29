package com.koliving.api.room.ui;

import com.koliving.api.room.application.RoomService;
import com.koliving.api.room.application.dto.RoomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[관리자] 룸 관리 API", description = "관리자 룸 관리 API")
@RestController
@RequestMapping("api/v1/management/rooms")
@RequiredArgsConstructor
public class RoomManageController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        List<RoomResponse> responses = roomService.list();
        return ResponseEntity.ok()
            .body(responses);
    }


}
