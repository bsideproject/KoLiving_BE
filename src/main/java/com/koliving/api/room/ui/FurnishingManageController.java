package com.koliving.api.room.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.room.application.FurnishingService;
import com.koliving.api.room.application.RoomTypeService;
import com.koliving.api.room.application.dto.FurnishingSaveRequest;
import com.koliving.api.room.application.dto.RoomTypeSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "[관리자] 가구 관리 API", description = "관리자 가구 관리 API")
@RestController
@RequestMapping("api/v1/management/furnishings")
@RequiredArgsConstructor
public class FurnishingManageController {

    private final FurnishingService furnishingService;

    @Operation(
        summary = "가구 등록",
        description = "가구을 등록합니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "가구 등록 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "가구 등록 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "가구 등록 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PostMapping
    public ResponseEntity<Long> save(@RequestBody FurnishingSaveRequest request) {
        final Long id = furnishingService.save(request);

        return ResponseEntity.created(URI.create("api/v1/management/furnishings" + id))
            .build();
    }
}
