package com.koliving.api.location.ui;

import com.koliving.api.base.ErrorResponse;
import com.koliving.api.location.application.LocationService;
import com.koliving.api.location.application.dto.LocationResponse;
import com.koliving.api.location.application.dto.LocationSaveRequest;
import com.koliving.api.location.application.dto.LocationUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "[관리자] Location 관리 API", description = "관리자 Location 관리 API")
@RestController
@RequestMapping("api/v1/management/locations")
@RequiredArgsConstructor
public class LocationManageController {

    private final LocationService locationService;

    @Operation(
        summary = "지역 리스트 조회",
        description = "지역 리스트를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "지역 조회 성공",
                content = @Content(schema = @Schema(implementation = LocationResponse.class))
            ),
        })
    @GetMapping
    public ResponseEntity<List<LocationResponse>> list() {
        final List<LocationResponse> responses = locationService.findAll();
        return ResponseEntity.ok()
            .body(responses);
    }

    @Operation(
        summary = "지역 등록",
        description = "지역 정보를 등록합니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "지역 등록 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "지역 등록 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PostMapping
    public ResponseEntity<Long> save(@RequestBody LocationSaveRequest request) {
        final Long id = locationService.save(request);

        return ResponseEntity.created(URI.create("api/v1/management/locations/" + id))
            .build();
    }

    @Operation(
        summary = "지역 상세 조회",
        description = "지역을 상세 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "지역 상세 조회 성공",
                content = @Content(schema = @Schema(implementation = LocationResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "지역 상세 조회 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @GetMapping("{id}")
    public ResponseEntity<LocationResponse> findById(@PathVariable Long id) {
        final LocationResponse response = locationService.findOne(id);

        return ResponseEntity.ok()
            .body(response);
    }

    @Operation(
        summary = "지역 정보 업데이트",
        description = "지역 정보를 업데이트 합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "지역 정보 변경 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "정보 변경 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
        })
    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody LocationUpdateRequest request) {
        locationService.update(id, request);

        return ResponseEntity.ok()
            .build();
    }

    @Operation(
        summary = "지역 정보 삭제",
        description = "지역 정보를 삭제합니다",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "지역 삭제 성공"
            )
        })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        locationService.deleteById(id);

        return ResponseEntity.noContent()
            .build();
    }
}
