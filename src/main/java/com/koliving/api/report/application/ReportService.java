package com.koliving.api.report.application;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.email.EmailService;
import com.koliving.api.email.IEmailService;
import com.koliving.api.properties.FrontProperties;
import com.koliving.api.report.application.dto.ReportReasonResponse;
import com.koliving.api.report.application.dto.ReportRequest;
import com.koliving.api.report.domain.ReportReason;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.infra.RoomRepository;
import com.koliving.api.user.domain.User;
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
    private final RoomRepository roomRepository;
    private final IEmailService emailService;
    private final FrontProperties frontProperties;

    public List<ReportReasonResponse> getReasons() {
        return reportReasonRepository.findAll()
            .stream()
            .map(ReportReasonResponse::of)
            .collect(Collectors.toList());
    }

    public void saveReport(ReportRequest request, User user) {
        final Room room = roomRepository.findById(request.roomId())
            .orElseThrow(() -> new KolivingServiceException(ServiceError.RECORD_NOT_EXIST));

        final ReportReason reportReason = reportReasonRepository.findById(request.reportId())
            .orElseThrow(() -> new KolivingServiceException(ServiceError.RECORD_NOT_EXIST));

        emailService.sendRoomReport(getRoomDetailUrl(room.getId()), reportReason.getName(), user);
    }

    public String getRoomDetailUrl(Long roomId) {
        return String.format("%s/room/%d", frontProperties.getOrigin(), roomId);
    }
}
