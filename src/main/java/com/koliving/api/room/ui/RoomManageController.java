package com.koliving.api.room.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.room.application.RoomService;
import com.koliving.api.room.application.dto.RoomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[관리자] 룸 관리 API", description = "관리자 룸 관리 API")
@RestController
@RequestMapping("api/v1/management/rooms")
@RequiredArgsConstructor
public class RoomManageController {
    private final RoomService roomService;

    @Operation(
        summary = "방 조회",
        description = "방을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "방 조회 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "방 조회 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        List<RoomResponse> responses = roomService.list();
        return ResponseEntity.ok()
            .body(responses);
    }

    @Operation(
        summary = "방 삭제",
        description = "방을 삭제합니다.",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "방 삭제 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "방 삭제 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}
