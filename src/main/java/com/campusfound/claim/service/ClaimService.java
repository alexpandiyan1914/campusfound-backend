package com.campusfound.claim.service;

import com.campusfound.claim.dto.ClaimResponse;
import com.campusfound.claim.dto.CreateClaimRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClaimService {

    ClaimResponse createClaim(CreateClaimRequest request);

    Page<ClaimResponse> getMyClaims(Pageable pageable);

    ClaimResponse getClaimById(Long id);

    Page<ClaimResponse> getAllClaims(Pageable pageable);

    Page<ClaimResponse> getPendingClaims(Pageable pageable);

    ClaimResponse approveClaim(Long id);

    ClaimResponse rejectClaim(Long id);
}