package com.campusfound.passwordreset.controller;

import com.campusfound.passwordreset.dto.ForgotPasswordRequest;
import com.campusfound.passwordreset.dto.ResetPasswordRequest;
import com.campusfound.passwordreset.dto.VerifyPasswordResetOtpRequest;
import com.campusfound.passwordreset.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(
        "/api/auth/forgot-password"
)
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService
            passwordResetService;

    /*
     * STEP 1:
     * Request OTP
     */
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @Valid
            @RequestBody
            ForgotPasswordRequest request) {

        passwordResetService
                .sendResetOtp(
                        request.getEmail()
                );

        return ResponseEntity.ok(
                "Password reset OTP sent successfully"
        );
    }

    /*
     * STEP 2:
     * Verify OTP and obtain reset token
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<
            Map<String, String>
            > verifyOtp(

            @Valid
            @RequestBody
            VerifyPasswordResetOtpRequest request) {

        String resetToken =
                passwordResetService
                        .verifyResetOtp(
                                request.getEmail(),
                                request.getOtp()
                        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP verified successfully",

                        "resetToken",
                        resetToken
                )
        );
    }

    /*
     * STEP 3:
     * Change password
     */
    @PostMapping("/reset")
    public ResponseEntity<String>
    resetPassword(

            @Valid
            @RequestBody
            ResetPasswordRequest request) {

        passwordResetService
                .resetPassword(
                        request.getResetToken(),
                        request.getNewPassword()
                );

        return ResponseEntity.ok(
                "Password reset successfully"
        );
    }
}