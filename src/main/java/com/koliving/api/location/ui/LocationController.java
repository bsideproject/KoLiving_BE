package com.koliving.api.location.ui;

import com.koliving.api.location.application.LocationService;
import com.koliving.api.location.application.dto.LocationResponse;
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

/**
 * author : haedoang date : 2023/08/13 description :
 */
@Tag(name = "지역 API", description = "Location API")
@RestController
@RequestMapping("api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

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
}
