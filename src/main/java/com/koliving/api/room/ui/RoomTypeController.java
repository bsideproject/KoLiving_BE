package com.koliving.api.room.ui;


import com.koliving.api.room.application.RoomTypeService;
import com.koliving.api.room.application.dto.RoomTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "룸 타입 API", description = "ROOM TYPE API")
@RestController
@RequestMapping("api/v1/room/types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @Operation(
        summary = "룸 타입 리스트 조회",
        description = "룸 타입 리스트를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "룸 타입 리스트 조회 성공",
                content = @Content(schema = @Schema(implementation = RoomTypeResponse.class))
            ),
        })
    @GetMapping
    public ResponseEntity<List<RoomTypeResponse>> list() {
        final List<RoomTypeResponse> responses = roomTypeService.list();
        return ResponseEntity.ok()
            .body(responses);
    }
}
