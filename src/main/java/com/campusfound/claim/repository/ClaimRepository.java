package com.campusfound.claim.repository;

import com.campusfound.claim.entity.Claim;
import com.campusfound.claim.entity.ClaimStatus;
import com.campusfound.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Page<Claim> findByClaimedBy(
            User user,
            Pageable pageable
    );

    Page<Claim> findByStatus(
            ClaimStatus status,
            Pageable pageable
    );

    boolean existsByItemIdAndClaimedById(
            Long itemId,
            Long userId
    );

    boolean existsByItemIdAndClaimedByIdAndStatus(
            Long itemId,
            Long userId,
            ClaimStatus status
    );

    boolean existsByItemIdAndStatus(
            Long itemId,
            ClaimStatus claimStatus
    );

    @Modifying
    @Query("""
            UPDATE Claim c
            SET c.status = com.campusfound.claim.entity.ClaimStatus.REJECTED
            WHERE c.item.id = :itemId
            AND c.id <> :approvedClaimId
            AND c.status = com.campusfound.claim.entity.ClaimStatus.PENDING
            """)
    void rejectOtherPendingClaims(
            @Param("itemId") Long itemId,
            @Param("approvedClaimId") Long approvedClaimId
    );
}