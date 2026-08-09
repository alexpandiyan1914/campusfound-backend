package com.campusfound.claim.service;

import com.campusfound.claim.dto.ClaimResponse;
import com.campusfound.claim.dto.CreateClaimRequest;
import com.campusfound.claim.entity.Claim;
import com.campusfound.claim.entity.ClaimStatus;
import com.campusfound.claim.repository.ClaimRepository;
import com.campusfound.item.entity.Item;
import com.campusfound.item.entity.ItemStatus;
import com.campusfound.item.repository.ItemRepository;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {

        User user = getCurrentUser();

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() ->
                        new RuntimeException("Item not found")
                );

        if (item.getStatus() != ItemStatus.ACTIVE) {
            throw new RuntimeException(
                    "Claims cannot be submitted for a closed item"
            );
        }

        if (claimRepository.existsByItemIdAndClaimedById(
                item.getId(),
                user.getId()
        )) {
            throw new RuntimeException(
                    "You have already submitted a claim for this item"
            );
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimedBy(user)
                .reason(request.getReason())
                .status(ClaimStatus.PENDING)
                .build();

        Claim savedClaim = claimRepository.save(claim);

        return mapToResponse(savedClaim);
    }

    @Override
    public Page<ClaimResponse> getMyClaims(Pageable pageable) {

        User user = getCurrentUser();

        return claimRepository
                .findByClaimedBy(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ClaimResponse getClaimById(Long id) {

        User user = getCurrentUser();

        Claim claim = getClaim(id);

        if (user.getRole().name().equals("STUDENT")
                && !claim.getClaimedBy().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to view this claim"
            );
        }

        return mapToResponse(claim);
    }

    @Override
    public Page<ClaimResponse> getAllClaims(Pageable pageable) {

        return claimRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ClaimResponse> getPendingClaims(Pageable pageable) {

        return claimRepository
                .findByStatus(ClaimStatus.PENDING, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ClaimResponse approveClaim(Long id) {

        Claim claim = getClaim(id);

        validatePending(claim);

        Item item = claim.getItem();

        if (item.getStatus() != ItemStatus.ACTIVE) {
            throw new RuntimeException(
                    "This item is no longer available for approval"
            );
        }

        if (claimRepository.existsByItemIdAndStatus(
                item.getId(),
                ClaimStatus.APPROVED
        )) {
            throw new RuntimeException(
                    "This item has already been claimed successfully"
            );
        }

        claim.setStatus(ClaimStatus.APPROVED);

        item.setStatus(ItemStatus.CLOSED);

        claimRepository.rejectOtherPendingClaims(
                item.getId(),
                claim.getId()
        );

        itemRepository.save(item);

        Claim updatedClaim = claimRepository.save(claim);

        return mapToResponse(updatedClaim);
    }

    @Override
    @Transactional
    public ClaimResponse rejectClaim(Long id) {

        Claim claim = getClaim(id);

        validatePending(claim);

        claim.setStatus(ClaimStatus.REJECTED);

        Claim updatedClaim = claimRepository.save(claim);

        return mapToResponse(updatedClaim);
    }

    private Claim getClaim(Long id) {

        return claimRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Claim not found")
                );
    }

    private void validatePending(Claim claim) {

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending claims can be approved or rejected"
            );
        }
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    private ClaimResponse mapToResponse(Claim claim) {

        return ClaimResponse.builder()
                .id(claim.getId())
                .itemId(claim.getItem().getId())
                .itemTitle(claim.getItem().getTitle())
                .claimedById(claim.getClaimedBy().getId())
                .claimedBy(claim.getClaimedBy().getFullName())
                .claimantEmail(claim.getClaimedBy().getEmail())
                .reason(claim.getReason())
                .status(claim.getStatus())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}