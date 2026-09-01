package com.campusfound.verification.service;

import com.campusfound.email.service.EmailService;
import com.campusfound.user.repository.UserRepository;
import com.campusfound.verification.entity.EmailVerification;
import com.campusfound.verification.entity.OtpPurpose;
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

    private final SecureRandom secureRandom =
            new SecureRandom();

    /*
     * Registration OTP
     */
    @Transactional
    public void sendOtp(String email) {

        String normalizedEmail =
                normalizeEmail(email);

        validateCollegeEmail(normalizedEmail);

        if (userRepository
                .findByEmail(normalizedEmail)
                .isPresent()) {

            throw new RuntimeException(
                    "An account already exists with this email"
            );
        }

        EmailVerification verification =
                verificationRepository
                        .findByEmail(normalizedEmail)
                        .orElse(null);

        /*
         * 60 second resend cooldown
         */
        if (verification != null
                && verification.getPurpose()
                == OtpPurpose.REGISTRATION
                && verification.getCreatedAt() != null) {

            LocalDateTime resendAllowedAt =
                    verification
                            .getCreatedAt()
                            .plusSeconds(60);

            if (LocalDateTime.now()
                    .isBefore(resendAllowedAt)) {

                throw new RuntimeException(
                        "Please wait before requesting another OTP"
                );
            }
        }

        String otp =
                generateOtp();

        String otpHash =
                passwordEncoder.encode(otp);

        if (verification == null) {

            verification =
                    EmailVerification.builder()
                            .email(normalizedEmail)
                            .build();
        }

        /*
         * Important:
         * this OTP is specifically for registration.
         */
        verification.setPurpose(
                OtpPurpose.REGISTRATION
        );

        verification.setOtpHash(
                otpHash
        );

        verification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(5)
        );

        verification.setVerified(false);
        verification.setAttempts(0);

        verification.setCreatedAt(
                LocalDateTime.now()
        );

        verificationRepository.save(
                verification
        );

        emailService.sendOtp(
                normalizedEmail,
                otp
        );
    }

    /*
     * Verify registration OTP
     */
    @Transactional
    public void verifyOtp(
            String email,
            String otp) {

        String normalizedEmail =
                normalizeEmail(email);

        EmailVerification verification =
                verificationRepository
                        .findByEmailAndPurpose(
                                normalizedEmail,
                                OtpPurpose.REGISTRATION
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Registration OTP request not found"
                                )
                        );

        if (verification.isVerified()) {
            return;
        }

        if (LocalDateTime.now()
                .isAfter(
                        verification.getExpiresAt()
                )) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP"
            );
        }

        if (verification.getAttempts() >= 5) {

            throw new RuntimeException(
                    "Too many incorrect attempts. Request a new OTP"
            );
        }

        boolean matches =
                passwordEncoder.matches(
                        otp,
                        verification.getOtpHash()
                );

        if (!matches) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            verificationRepository.save(
                    verification
            );

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        verification.setVerified(true);

        verificationRepository.save(
                verification
        );
    }

    /*
     * Used by register() before creating the User.
     */
    public boolean isEmailVerified(
            String email) {

        String normalizedEmail =
                normalizeEmail(email);

        return verificationRepository
                .findByEmailAndPurpose(
                        normalizedEmail,
                        OtpPurpose.REGISTRATION
                )
                .map(
                        EmailVerification::isVerified
                )
                .orElse(false);
    }

    /*
     * Called after successful registration.
     */
    @Transactional
    public void removeVerification(
            String email) {

        verificationRepository
                .deleteByEmailAndPurpose(
                        normalizeEmail(email),
                        OtpPurpose.REGISTRATION
                );
    }

    private String generateOtp() {

        int number =
                secureRandom.nextInt(900000)
                        + 100000;

        return String.valueOf(number);
    }

    private String normalizeEmail(
            String email) {

        return email
                .trim()
                .toLowerCase();
    }

    private void validateCollegeEmail(
            String email) {

        boolean valid =
                email.endsWith(
                        "@student.tce.edu"
                )
                        ||
                        email.endsWith(
                                "@tce.edu"
                        );

        if (!valid) {

            throw new RuntimeException(
                    "Please use a valid TCE email address"
            );
        }
    }
}