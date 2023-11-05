package com.koliving.api.user.application.dto;

import static com.koliving.api.user.domain.NotifyType.RECEIVE;
import static com.koliving.api.user.domain.NotifyType.SEND;

import com.koliving.api.user.domain.Notification;
import com.koliving.api.user.domain.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * author : haedoang date : 2023/11/05 description :
 */
public record NotificationResponse(

    @Schema(description = "알림 타입[SEND, RECEIVE]")
    NotifyType type,

    @Schema(description = "알림 발/수신자")
    String userName,

    @Schema(description = "알림 생성일자")
    LocalDateTime createdAt
) {

    public static NotificationResponse of(NotifyType type, Notification entity) {
        return new NotificationResponse(
            type,
            type.isSend() ? entity.getSender().getFullName() : entity.getReceiver().getFullName(),
            entity.getCreatedAt()
        );
    }

    public static List<NotificationResponse> ofList(List<Notification> receives, List<Notification> sent) {
        return Stream.concat(
            receives.stream()
                .map(notification -> of(RECEIVE, notification)
            ),
            sent.stream()
                .map(notification -> of(SEND, notification))
            ).sorted(Comparator.comparing(NotificationResponse::createdAt).reversed())
            .collect(Collectors.toList());
    }
}
