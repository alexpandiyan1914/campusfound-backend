package com.campusfound.claim.dto;

import com.campusfound.claim.entity.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimResponse {

    private Long id;

    private Long itemId;

    private String itemTitle;

    private Long claimedById;

    private String claimedBy;

    private String claimantEmail;

    private String reason;

    private ClaimStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}