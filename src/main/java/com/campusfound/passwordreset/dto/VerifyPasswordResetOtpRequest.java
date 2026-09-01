package com.campusfound.passwordreset.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPasswordResetOtpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String otp;
}