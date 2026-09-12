package com.campusfound.auth.service;

import com.campusfound.auth.dto.AuthResponse;
import com.campusfound.auth.dto.LoginRequest;
import com.campusfound.auth.dto.RegisterRequest;
import com.campusfound.security.jwt.JwtService;
import com.campusfound.user.entity.Role;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import com.campusfound.verification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    public AuthResponse register(RegisterRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (!emailVerificationService.isEmailVerified(email)) {
            throw new RuntimeException(
                    "Email verification is required before registration"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "An account already exists with this email"
            );
        }

        User user = User.builder()
                .fullName(
                        request.getFullName().trim()
                )
                .email(email)
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .phone(
                        request.getPhone().trim()
                )
                .department(
                        request.getDepartment().trim()
                )
                .year(
                        request.getYear()
                )
                .role(Role.STUDENT)
                .build();

        userRepository.save(user);

        emailVerificationService.removeVerification(email);

        return new AuthResponse(
                null,
                "User registered successfully"
        );
    }

    public AuthResponse login(LoginRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Login successful"
        );
    }
}