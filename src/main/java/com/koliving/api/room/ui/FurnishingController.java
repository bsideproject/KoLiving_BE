//package com.koliving.api.room.ui;
//
//import com.koliving.api.room.application.FurnishingService;
//import com.koliving.api.room.application.dto.FurnishingResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@Tag(name = "가구 API", description = "FURNISHING API")
//@RestController
//@RequestMapping("api/v1/furnishings")
//@RequiredArgsConstructor
//public class FurnishingController {
//
//    private final FurnishingService furnishingService;
//
//    @Operation(
//        summary = "가구 리스트 조회",
//        description = "가구 리스트를 조회합니다.",
//        responses = {
//            @ApiResponse(
//                responseCode = "200",
//                description = "가구 리스트 조회 성공",
//                content = @Content(schema = @Schema(implementation = FurnishingResponse.class))
//            ),
//        })
//    @GetMapping
//    public ResponseEntity<List<FurnishingResponse>> list() {
//        final List<FurnishingResponse> responses = furnishingService.list();
//        return ResponseEntity.ok()
//            .body(responses);
//    }
//}
