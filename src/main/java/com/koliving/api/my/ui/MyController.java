package com.koliving.api.my.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.my.application.dto.UserProfileUpdateRequest;
import com.koliving.api.room.application.RoomService;
import com.koliving.api.room.application.dto.RoomResponse;
import com.koliving.api.user.application.dto.NotificationResponse;
import com.koliving.api.user.domain.User;
import com.koliving.api.user.application.UserService;
import com.koliving.api.user.application.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "MY API", description = "MY API")
@RestController
@RequestMapping("api/v1/my")
@RequiredArgsConstructor
public class MyController {

    private final UserService userService;
    private final RoomService roomService;

    @Operation(
        summary = "프로필 수정",
        description = "프로필을 수정합니다.",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "프로필 수정 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "프로필 수정 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody UserProfileUpdateRequest request,
        @AuthenticationPrincipal User user) {
        userService.updateProfile(request, user.getId());
        return ResponseEntity.noContent().build();
    }


    @Operation(
        summary = "프로필 조회",
        description = "프로필 정보를 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "프로필 조회 성공",
                content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "프로필 조회 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @GetMapping
    public ResponseEntity<UserResponse> myProfile(@AuthenticationPrincipal User user) {
        UserResponse response = userService.findById(user.getId());

        return ResponseEntity.ok()
            .body(response);
    }

    @Operation(
        summary = "좋아요 게시글 조회",
        description = "좋아요 한 게시글 리스트를 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "좋아요 게시글 조회 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "좋아요 게시글 조회 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @GetMapping("/rooms/like")
    public ResponseEntity<Page<RoomResponse>> getLikedRooms(Pageable pageable, @AuthenticationPrincipal User user) {
        final Page<RoomResponse> responses = roomService.findLikeRoomByUser(pageable, user);

        return ResponseEntity.ok()
            .body(responses);
    }

    @Operation(
        summary = "알림 조회",
        description = "알림 리스트 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "알림 리스트 조회 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "알림 리스트 조회 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @GetMapping("/notification")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal User user) {
        List<NotificationResponse> responses = userService.getNotifications(user);
        return ResponseEntity.ok()
            .body(responses);
    }
}
