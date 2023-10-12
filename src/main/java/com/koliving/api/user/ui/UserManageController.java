package com.koliving.api.user.ui;

import com.koliving.api.user.application.UserService;
import com.koliving.api.user.application.dto.UserResponse;
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

//TODO auth scope -> ROLE_MANAGER
@Tag(name = "[관리자] 회원 관리 API", description = "관리자 회원 관리 API")
@RestController
@RequestMapping("api/v1/management/users")
@RequiredArgsConstructor
public class UserManageController {

    private final UserService userService;

    @Operation(
        summary = "회원 리스트 조회",
        description = "회원 리스트를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "회원 조회 성공",
                content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
        })
    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        final List<UserResponse> responses = userService.list();

        return ResponseEntity.ok()
            .body(responses);
    }

}
