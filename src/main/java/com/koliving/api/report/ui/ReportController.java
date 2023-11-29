package com.koliving.api.report.ui;


import com.koliving.api.base.ErrorResponse;
import com.koliving.api.report.application.ReportService;
import com.koliving.api.report.application.dto.ReportReasonResponse;
import com.koliving.api.report.application.dto.ReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리포트 API", description = "REPORT API")
@RequestMapping("api/v1/report")
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(
        summary = "리포트 사유 목록",
        description = "리포트 사유를 요청합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "리포트 사유 요청 성공",
                content = @Content(schema = @Schema(implementation = ReportReasonResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "리포트 사유 요청 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    @GetMapping("/reasons")
    public ResponseEntity<List<ReportReasonResponse>> getReasons() {
        final List<ReportReasonResponse> responses = reportService.getReasons();
        return ResponseEntity.ok()
            .body(responses);
    }


    @Operation(
        summary = "리포트 요청",
        description = "리포트를 요청합니다",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "리포트 요청 성공"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "리포트 요청 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    @PostMapping("/reasons")
    public ResponseEntity<Void> report(@RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
