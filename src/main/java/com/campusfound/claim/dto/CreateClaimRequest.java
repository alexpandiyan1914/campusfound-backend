package com.campusfound.claim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateClaimRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Claim reason is required")
    private String reason;
}