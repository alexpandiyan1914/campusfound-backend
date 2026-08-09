package com.campusfound.item.dto;

import com.campusfound.item.entity.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponse {

    private Long id;

    private String title;

    private String description;

    private String category;

    private String location;

    private LocalDate lostFoundDate;

    private ItemStatus status;

    private String imageUrl;

    private String reportedBy;

    private LocalDateTime createdAt;
}