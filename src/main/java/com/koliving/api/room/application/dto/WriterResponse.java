package com.koliving.api.room.application.dto;

import com.koliving.api.user.Gender;
import com.koliving.api.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "작성자 정보")
public record WriterResponse(
    @Schema(description = "작성자 이름")
    String firstName,
    @Schema(description = "작성자 성")
    String lastName,

    @Schema(description = "작성자 성별")
    Gender gender,
    @Schema(description = "작성자 생년월일")
    LocalDate birthDate,
    @Schema(description = "작성자 프로필 URL")
    String imageUrl
) {

    public static WriterResponse of(User entity) {
        return new WriterResponse(entity.getFirstName(), entity.getLastName(), entity.getGender(), entity.getBirthDate(), entity.getImageUrl());
    }
}
