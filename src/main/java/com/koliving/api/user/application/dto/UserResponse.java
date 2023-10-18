package com.koliving.api.user.application.dto;


import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.user.Gender;
import com.koliving.api.user.SignUpStatus;
import com.koliving.api.user.User;
import com.koliving.api.user.UserRole;

import java.time.LocalDate;

public record UserResponse(Long id, String email, String firstName, String lastName, Gender gender, LocalDate birthDate,
                           String description, ImageFile imageFile, UserRole userRole, SignUpStatus signUpStatus) {

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
