package com.example.be.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.be.entity.User;
import com.example.be.enums.Role;

public record UserResponse(
        UUID id,
        String email,
        Role role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
