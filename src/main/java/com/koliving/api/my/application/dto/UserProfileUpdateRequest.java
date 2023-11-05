package com.koliving.api.my.application.dto;

import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.user.domain.Gender;
import com.koliving.api.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "작성자 정보")
public record UserProfileUpdateRequest(

    @NotNull
    @Schema(description = "이미지 URL 고유 Key")
    Long profileId,

    @NotNull
    @Schema(description = "성별")
    Gender gender,

    @NotNull
    @Schema(description = "이름")
    String firstName,

    @NotNull
    @Schema(description = "성")
    String lastName,

    @NotNull
    @Schema(description = "생년월일")
    LocalDate birthDate,

    @Nullable
    @Schema(description = "설명")
    String description
) {
    public User toUser(ImageFile imageFile) {
        return User.of(imageFile, gender, firstName, lastName, birthDate, description);
    }
}
