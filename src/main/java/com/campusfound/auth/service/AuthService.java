package com.campusfound.auth.service;

import com.campusfound.auth.dto.AuthResponse;
import com.campusfound.auth.dto.RegisterRequest;
import com.campusfound.user.entity.Role;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .department(request.getDepartment())
                .year(request.getYear())
                .role(Role.STUDENT)
                .build();

        userRepository.save(user);

        return new AuthResponse("User registered successfully");
    }
}