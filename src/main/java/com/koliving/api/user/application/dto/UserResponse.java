package com.koliving.api.user.application.dto;


import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.user.domain.Gender;
import com.koliving.api.user.domain.SignUpStatus;
import com.koliving.api.user.domain.User;
import com.koliving.api.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "회원 정보")
public record UserResponse(
    @Schema(description = "회원 고유 ID")
    Long id,

    @Schema(description = "회원 EMAIL")
    String email,

    @Schema(description = "회원 이름")
    String firstName,

    @Schema(description = "회원 성")
    String lastName,

    @Schema(description = "회원 성별")
    Gender gender,

    @Schema(description = "회원 생년월일")
    LocalDate birthDate,

    @Schema(description = "회원 자기소개")
    String description,

    @Schema(description = "회원 이미지파일 정보")
    ImageFile imageFile,

    @Schema(description = "회원 권한")
    UserRole userRole,

    @Schema(description = "회원 가입 상태")
    SignUpStatus signUpStatus
) {

    public static UserResponse valueOf(User entity) {
        return new UserResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getGender(),
            entity.getBirthDate(),
            entity.getDescription(),
            entity.getImageFile(),
            entity.getUserRole(),
            entity.getSignUpStatus()
        );
    }
}
