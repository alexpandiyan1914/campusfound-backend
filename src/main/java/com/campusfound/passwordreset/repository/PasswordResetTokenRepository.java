package com.campusfound.passwordreset.repository;

import com.campusfound.passwordreset.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(
            String token
    );

    void deleteByEmail(
            String email
    );
}