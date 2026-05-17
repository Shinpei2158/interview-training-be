package com.example.be.user.dto;

import com.example.be.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotNull
    private Role role;

    @NotNull
    private Boolean active;

    @Size(min = 8, max = 100)
    private String password;
}
