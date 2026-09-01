package com.campusfound.user.controller;

import com.campusfound.backend.dto.ChangePasswordRequest;
import com.campusfound.user.dto.UpdateProfileRequest;
import com.campusfound.user.dto.UserResponse;
import com.campusfound.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        return ResponseEntity.ok(
                userService.getCurrentUser()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(
                userService.updateProfile(request)
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(
                userDetails.getUsername(),
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}