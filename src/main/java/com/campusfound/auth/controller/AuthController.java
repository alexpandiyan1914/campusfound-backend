package com.campusfound.auth.controller;

import com.campusfound.auth.dto.AuthResponse;
import com.campusfound.auth.dto.RegisterRequest;
import com.campusfound.auth.dto.LoginRequest;
import com.campusfound.auth.service.AuthService;
import com.campusfound.verification.dto.SendOtpRequest;
import com.campusfound.verification.dto.VerifyOtpRequest;
import com.campusfound.verification.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        emailVerificationService.sendOtp(
                request.getEmail()
        );

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        emailVerificationService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}