package com.koliving.api.room.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.room.application.RoomService;
import com.koliving.api.room.application.dto.RoomSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "방 API", description = "ROOM API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    @Operation(
        summary = "방 등록",
        description = "방을 등록합니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "방 등록 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "방 등록 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PostMapping
    public ResponseEntity<Long> save(@RequestBody RoomSaveRequest request) {
        final Long id = roomService.save(request);

        return ResponseEntity.created(URI.create("api/v1/rooms/" + id))
            .build();
    }
}
