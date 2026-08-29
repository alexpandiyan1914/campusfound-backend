package com.campusfound.verification.service;

import com.campusfound.email.service.EmailService;
import com.campusfound.user.repository.UserRepository;
import com.campusfound.verification.entity.EmailVerification;
import com.campusfound.verification.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void sendOtp(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        validateCollegeEmail(normalizedEmail);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException(
                    "An account already exists with this email"
            );
        }

        EmailVerification verification =
                verificationRepository
                        .findByEmail(normalizedEmail)
                        .orElse(null);

        // RESEND COOLDOWN
        if (verification != null
                && verification.getCreatedAt() != null) {

            LocalDateTime resendAllowedAt =
                    verification.getCreatedAt()
                            .plusSeconds(60);

            if (LocalDateTime.now()
                    .isBefore(resendAllowedAt)) {

                throw new RuntimeException(
                        "Please wait before requesting another OTP"
                );
            }
        }

        String otp = generateOtp();

        String otpHash =
                passwordEncoder.encode(otp);

        if (verification == null) {

            verification =
                    EmailVerification.builder()
                            .email(normalizedEmail)
                            .build();
        }

        verification.setOtpHash(otpHash);
        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );
        verification.setVerified(false);
        verification.setAttempts(0);
        verification.setCreatedAt(
                LocalDateTime.now()
        );

        verificationRepository.save(verification);

        emailService.sendOtp(
                normalizedEmail,
                otp
        );
    }

    @Transactional
    public void verifyOtp(
            String email,
            String otp) {

        String normalizedEmail =
                email.trim().toLowerCase();

        EmailVerification verification =
                verificationRepository
                        .findByEmail(normalizedEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "OTP verification request not found"
                                )
                        );

        if (verification.isVerified()) {
            return;
        }

        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP"
            );
        }

        if (verification.getAttempts() >= 5) {
            throw new RuntimeException(
                    "Too many incorrect attempts. Request a new OTP"
            );
        }

        if (!passwordEncoder.matches(
                otp,
                verification.getOtpHash())) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            verificationRepository.save(verification);

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        verification.setVerified(true);

        verificationRepository.save(verification);
    }

    public boolean isEmailVerified(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        return verificationRepository
                .findByEmail(normalizedEmail)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    @Transactional
    public void removeVerification(String email) {

        verificationRepository.deleteByEmail(
                email.trim().toLowerCase()
        );
    }

    private void validateCollegeEmail(String email) {

        boolean valid = email.endsWith("@student.tce.edu") || email.endsWith("@tce.edu");

        if (!valid) {
            throw new RuntimeException(
                    "Please use a valid TCE email address"
            );
        }
    }

    private String generateOtp() {

        int number = secureRandom.nextInt(900000) + 100000;

        return String.valueOf(number);
    }
}