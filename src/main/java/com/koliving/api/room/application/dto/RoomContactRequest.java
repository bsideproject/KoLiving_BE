package com.koliving.api.room.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * author : haedoang date : 2023/11/01 description :
 */
@Schema(description = "방 contact 정보")
public record RoomContactRequest(
    @Schema(description = "방 ID")
    Long roomId,

    @Schema(description = "연락처 정보")
    String contactInfo,

    @Schema(description = "연락처 메시지")
    String message
) {

}
