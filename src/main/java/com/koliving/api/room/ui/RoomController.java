package com.koliving.api.room.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.room.application.RoomService;
import com.koliving.api.room.application.dto.RoomContactRequest;
import com.koliving.api.room.application.dto.RoomResponse;
import com.koliving.api.room.application.dto.RoomSaveRequest;
import com.koliving.api.room.application.dto.RoomSearchCondition;
import com.koliving.api.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<Long> save(@RequestBody RoomSaveRequest request, @AuthenticationPrincipal User user) {
        final Long id = roomService.save(request, user);

        return ResponseEntity.created(URI.create("api/v1/rooms/" + id))
            .build();
    }

    @Operation(
        summary = "방 검색",
        description = "방을 검색합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "방 검색 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "방 검색 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PostMapping("/search")
    public ResponseEntity<Page<RoomResponse>> search(@ParameterObject @PageableDefault Pageable pageable,
        @ParameterObject RoomSearchCondition condition) {
        return ResponseEntity.ok()
            .body(roomService.search(pageable, condition));
    }

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
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok()
            .body(roomService.findOne(id));
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
    public ResponseEntity<Void> deleteById(Long id, @AuthenticationPrincipal User user) {
        roomService.deleteRoomById(id, user);
        return ResponseEntity.noContent().build();
    }

    //TODO 멱등성 위배... 요청을 분리할까 고민
    @Operation(
        summary = "방 좋아요",
        description = "방을 좋아요 합니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "방 좋아요 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "방 좋아요 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PutMapping("/{id}/liked")
    public ResponseEntity<Void> likeRoom(@PathVariable Long id, @AuthenticationPrincipal User user) {
        roomService.likeRoom(id, user);
        return ResponseEntity.noContent()
            .build();
    }

    @Operation(
        summary = "방 연락하기",
        description = "방 소유자에게 연락을 한다",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "방 연락 요청 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "방 연락 요청 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PostMapping("/{id}/contact")
    public ResponseEntity<Void> contactRoom(@RequestBody RoomContactRequest request, @AuthenticationPrincipal User user) {
        roomService.contact(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .build();
    }
}
