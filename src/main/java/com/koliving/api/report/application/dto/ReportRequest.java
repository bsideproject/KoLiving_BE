package com.koliving.api.report.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * author : haedoang date : 2023/11/29 description :
 */
@Schema(description = "리포트 사유 조회")
public record ReportRequest(
    @Schema(description = "리포트 대상 룸 ID")
    Long roomId,

    @Schema(description = "리포트 사유 ID")
    Long reportId
) {
}
