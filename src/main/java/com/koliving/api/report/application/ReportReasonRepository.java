package com.koliving.api.report.application;

import com.koliving.api.report.domain.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author : haedoang date : 2023/11/29 description :
 */
public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {

}
