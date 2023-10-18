package com.koliving.api.my.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.my.application.dto.UserProfileUpdateRequest;
import com.koliving.api.user.User;
import com.koliving.api.user.application.UserService;
import com.koliving.api.user.application.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Void> updateProfile(@RequestBody UserProfileUpdateRequest request, @AuthenticationPrincipal User user) {
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
}
