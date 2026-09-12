package com.campusfound.user.service;

import com.campusfound.backend.dto.ChangePasswordRequest;
import com.campusfound.user.dto.UpdateProfileRequest;
import com.campusfound.user.dto.UserResponse;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getCurrentUser() {

        String email = getAuthenticatedEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateProfile(
            UpdateProfileRequest request
    ) {

        String email = getAuthenticatedEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (request.getFullName() != null) {
            user.setFullName(
                    request.getFullName().trim()
            );
        }

        if (request.getPhone() != null) {
            user.setPhone(
                    request.getPhone().trim()
            );
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    @Override
    public void changePassword(
            String email,
            ChangePasswordRequest request
    ) {

        User user = userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "New password cannot be the same as current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    private String getAuthenticatedEmail() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()
                .trim()
                .toLowerCase();
    }

    private UserResponse mapToResponse(
            User user
    ) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .year(user.getYear())
                .role(user.getRole())
                .build();
    }
}