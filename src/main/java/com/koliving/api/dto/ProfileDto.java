package com.koliving.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "회원가입 - 개인정보 작성")
@Getter
public class ProfileDto {

    @NotBlank
    @Schema(description = "이름", example = "gildong")
    private String firstName;

    @NotBlank
    @Schema(description = "성", example = "hong")
    private String lastName;

    @NotNull
    @Range(min = 0L, max=2L)
    @Schema(description = "성별 (0-남, 1-여, 2-OTHER)", allowableValues = {"0", "1", "2"}, example = "0")
    private int genderCode;

    @NotNull
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생년월일", maxLength = 10, example = "1990-03-02 (yyyy-MM-dd)")
    private LocalDate birthDate;

    @NotBlank
    @Size(min = 0, max =500)
    @Schema(description = "자기소개 (1000byte 까지)", maxLength = 500, example = "hello koliving")
    private String description;
}
