package com.campusfound.passwordreset.service;

import com.campusfound.email.service.EmailService;
import com.campusfound.passwordreset.entity.PasswordResetToken;
import com.campusfound.passwordreset.repository.PasswordResetTokenRepository;
import com.campusfound.user.entity.User;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;

    private final EmailVerificationRepository
            verificationRepository;

    private final PasswordResetTokenRepository
            resetTokenRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    /*
     * STEP 1:
     * Send password reset OTP.
     */
    @Transactional
    public void sendResetOtp(
            String email) {

        String normalizedEmail =
                normalizeEmail(email);

        /*
         * Forgot password is only for
         * EXISTING accounts.
         */
        userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );

        /*
         * Since one email has one temporary
         * verification row, find existing record.
         */
        EmailVerification verification =
                verificationRepository
                        .findByEmail(
                                normalizedEmail
                        )
                        .orElse(null);

        /*
         * Apply cooldown only if the currently
         * stored OTP is also PASSWORD_RESET.
         */
        if (verification != null
                && verification.getPurpose()
                == OtpPurpose.PASSWORD_RESET
                && verification.getCreatedAt()
                != null) {

            LocalDateTime resendAllowedAt =
                    verification
                            .getCreatedAt()
                            .plusSeconds(60);

            if (LocalDateTime.now()
                    .isBefore(
                            resendAllowedAt
                    )) {

                throw new RuntimeException(
                        "Please wait before requesting another OTP"
                );
            }
        }

        String otp =
                generateOtp();

        String otpHash =
                passwordEncoder.encode(
                        otp
                );

        if (verification == null) {

            verification =
                    EmailVerification.builder()
                            .email(
                                    normalizedEmail
                            )
                            .build();
        }

        /*
         * This changes/replaces the purpose
         * of this temporary OTP record.
         */
        verification.setPurpose(
                OtpPurpose.PASSWORD_RESET
        );

        verification.setOtpHash(
                otpHash
        );

        verification.setVerified(
                false
        );

        verification.setAttempts(
                0
        );

        /*
         * OTP expires after 5 minutes.
         */
        verification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(5)
        );

        verification.setCreatedAt(
                LocalDateTime.now()
        );

        verificationRepository.save(
                verification
        );

        /*
         * Send OTP through Brevo.
         */
        emailService.sendPasswordResetOtp(
                normalizedEmail,
                otp
        );
    }

    /*
     * STEP 2:
     * Verify password reset OTP.
     *
     * Returns a temporary reset token.
     */
    @Transactional
    public String verifyResetOtp(
            String email,
            String otp) {

        String normalizedEmail =
                normalizeEmail(email);

        EmailVerification verification =
                verificationRepository
                        .findByEmailAndPurpose(
                                normalizedEmail,
                                OtpPurpose.PASSWORD_RESET
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Password reset OTP request not found"
                                )
                        );

        /*
         * Check expiry.
         */
        if (LocalDateTime.now()
                .isAfter(
                        verification
                                .getExpiresAt()
                )) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP"
            );
        }

        /*
         * Maximum incorrect attempts.
         */
        if (verification
                .getAttempts() >= 5) {

            throw new RuntimeException(
                    "Too many incorrect attempts. Request a new OTP"
            );
        }

        /*
         * Compare entered OTP with hashed OTP.
         */
        boolean otpMatches =
                passwordEncoder.matches(
                        otp,
                        verification
                                .getOtpHash()
                );

        if (!otpMatches) {

            verification.setAttempts(
                    verification
                            .getAttempts()
                            + 1
            );

            verificationRepository.save(
                    verification
            );

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        verification.setVerified(
                true
        );

        verificationRepository.save(
                verification
        );

        /*
         * Remove any previous reset token
         * belonging to this email.
         */
        resetTokenRepository
                .deleteByEmail(
                        normalizedEmail
                );

        /*
         * Generate one-time reset token.
         */
        String token =
                UUID.randomUUID()
                        .toString();

        PasswordResetToken resetToken =
                PasswordResetToken
                        .builder()
                        .email(
                                normalizedEmail
                        )
                        .token(
                                token
                        )
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(10)
                        )
                        .used(false)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        resetTokenRepository.save(
                resetToken
        );

        return token;
    }

    /*
     * STEP 3:
     * Actually change the password.
     */
    @Transactional
    public void resetPassword(
            String resetTokenValue,
            String newPassword) {

        PasswordResetToken resetToken =
                resetTokenRepository
                        .findByToken(
                                resetTokenValue
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid password reset token"
                                )
                        );

        if (resetToken.isUsed()) {

            throw new RuntimeException(
                    "Password reset token has already been used"
            );
        }

        if (LocalDateTime.now()
                .isAfter(
                        resetToken
                                .getExpiresAt()
                )) {

            throw new RuntimeException(
                    "Password reset token has expired"
            );
        }

        User user =
                userRepository
                        .findByEmail(
                                resetToken
                                        .getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        /*
         * Never save the raw new password.
         */
        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(
                user
        );

        /*
         * Token becomes unusable after
         * successful password reset.
         */
        resetToken.setUsed(
                true
        );

        resetTokenRepository.save(
                resetToken
        );

        /*
         * Remove temporary OTP verification.
         */
        verificationRepository
                .deleteByEmailAndPurpose(
                        resetToken.getEmail(),
                        OtpPurpose.PASSWORD_RESET
                );
    }

    private String generateOtp() {

        int number =
                secureRandom.nextInt(
                        900000
                ) + 100000;

        return String.valueOf(
                number
        );
    }

    private String normalizeEmail(
            String email) {

        return email
                .trim()
                .toLowerCase();
    }
}