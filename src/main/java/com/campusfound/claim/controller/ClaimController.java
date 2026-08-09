package com.campusfound.claim.controller;

import com.campusfound.claim.dto.ClaimResponse;
import com.campusfound.claim.dto.CreateClaimRequest;
import com.campusfound.claim.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ClaimResponse> createClaim(
            @Valid @RequestBody CreateClaimRequest request) {

        return ResponseEntity.ok(
                claimService.createClaim(request)
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<ClaimResponse>> getMyClaims(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt"
            ) Pageable pageable) {

        return ResponseEntity.ok(
                claimService.getMyClaims(pageable)
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ClaimResponse> getClaimById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.getClaimById(id)
        );
    }

    // ADMIN

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClaimResponse>> getAllClaims(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt"
            ) Pageable pageable) {

        return ResponseEntity.ok(
                claimService.getAllClaims(pageable)
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClaimResponse>> getPendingClaims(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt"
            ) Pageable pageable) {

        return ResponseEntity.ok(
                claimService.getPendingClaims(pageable)
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> approveClaim(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.approveClaim(id)
        );
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> rejectClaim(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.rejectClaim(id)
        );
    }
}