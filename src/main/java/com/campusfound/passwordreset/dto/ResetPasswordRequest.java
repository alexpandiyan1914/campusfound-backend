package com.campusfound.passwordreset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String resetToken;

    @NotBlank
    @Size(
            min = 8,
            message = "Password must contain at least 8 characters"
    )
    private String newPassword;
}