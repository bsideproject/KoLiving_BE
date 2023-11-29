package com.koliving.api.report.application.dto;

import com.koliving.api.report.domain.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * author : haedoang date : 2023/11/29 description :
 */
@Schema(description = "리포트 사유 조회")
public record ReportReasonResponse(
    @Schema(description = "리포트 사유 고유 ID")
    Long id,

    @Schema(description = "리포트 사유 설명")
    String desc
) {

    public static ReportReasonResponse of(ReportReason entity) {
        return new ReportReasonResponse(entity.getId(), entity.getName());
    }
}
