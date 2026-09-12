package com.campusfound.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(
            min = 3,
            max = 100,
            message = "Full name must be between 3 and 100 characters"
    )
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Pattern(
            regexp = "(?i)^[A-Z0-9._%+-]+@(student\\.tce\\.edu|tce\\.edu)$",
            message = "Please use your TCE email address"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            max = 72,
            message = "Password must contain at least 8 characters"
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,72}$",
            message = "Password must contain a letter, number and special character"
    )
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Department / programme is required")
    @Size(
            min = 2,
            max = 100,
            message = "Department / programme must be between 2 and 100 characters"
    )
    private String department;

    @NotNull(message = "Year is required")
    @Min(
            value = 1,
            message = "Year must be between 1 and 5"
    )
    @Max(
            value = 5,
            message = "Year must be between 1 and 5"
    )
    private Integer year;
}