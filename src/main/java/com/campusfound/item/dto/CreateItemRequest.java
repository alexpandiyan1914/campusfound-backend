package com.campusfound.item.dto;

import com.campusfound.item.entity.ItemType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateItemRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String location;

    @NotNull
    private LocalDate lostFoundDate;

    @NotNull
    private ItemType type;

    private String imageUrl;

}