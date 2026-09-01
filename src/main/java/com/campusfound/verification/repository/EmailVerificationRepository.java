package com.campusfound.verification.repository;

import com.campusfound.verification.entity.EmailVerification;
import com.campusfound.verification.entity.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmail(String email);

    Optional<EmailVerification> findByEmailAndPurpose(
            String email,
            OtpPurpose purpose
    );

    void deleteByEmail(String email);

    void deleteByEmailAndPurpose(
            String email,
            OtpPurpose purpose
    );
}