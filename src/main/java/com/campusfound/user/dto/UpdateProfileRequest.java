package com.campusfound.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String fullName;

    private String phone;
}