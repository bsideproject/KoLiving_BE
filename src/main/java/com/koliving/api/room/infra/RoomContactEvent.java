package com.koliving.api.room.infra;

import com.koliving.api.base.KLDescription;
import com.koliving.api.room.domain.Room;
import com.koliving.api.user.domain.Gender;
import com.koliving.api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : haedoang date : 2023/11/05 description :
 */
@Getter
@AllArgsConstructor
public class RoomContactEvent {

    @KLDescription(value = "수신자 이메일 주소", example = "koliving@koliving.kr")
    private String to;

    @KLDescription(value = "방 ID")
    private Long roomId;

    @KLDescription(value = "연락방법", example = "01011112222")
    private String contactInfo;

    @KLDescription(value = "메시지", example = "방에 관심이 있어요^_^")
    private String message;

    @KLDescription(value = "발송자 이름", example = "David Beckham")
    private String senderName;

    @KLDescription(value = "발송자 연령", example = "30")
    private Integer senderAge;

    @KLDescription(value = "발송자 프로파일 이미지 주소")
    private String senderImageProfile;

    @KLDescription(value = "발송자 성별", example = "FEMALE")
    private Gender senderGender;

    @KLDescription(value = "발송자 자기소개")
    private String senderDescription;

    public static RoomContactEvent of(Room room, String contactInfo, String message, User sender) {
        return new RoomContactEvent(
            room.getUser().getEmail(),
            room.getId(),
            contactInfo,
            message,
            sender.getFullName(),
            sender.getAge(),
            sender.getImageProfile(),
            sender.getGender(),
            sender.getDescription()
        );
    }
}
