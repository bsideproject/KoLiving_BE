package com.koliving.api.report.application;

import com.koliving.api.report.application.dto.ReportReasonResponse;
import com.koliving.api.report.application.dto.ReportRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author : haedoang date : 2023/11/29 description :
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportReasonRepository reportReasonRepository;

    public List<ReportReasonResponse> getReasons() {
        return reportReasonRepository.findAll()
            .stream()
            .map(ReportReasonResponse::of)
            .collect(Collectors.toList());
    }

    public void saveReport(ReportRequest request) {
        //TODO 리포트 로직 구현
    }
}
