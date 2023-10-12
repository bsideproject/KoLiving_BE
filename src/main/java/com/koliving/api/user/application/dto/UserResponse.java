package com.koliving.api.user.application.dto;


import com.koliving.api.user.User;

public record UserResponse(Long id, String email, String imageUrl) {

    public static UserResponse valueOf(User entity) {
        return new UserResponse(entity.getId(), entity.getEmail(), entity.getImageUrl());
    }
}
