package com.campusfound.user.dto;

import com.campusfound.user.entity.Department;
import com.campusfound.user.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private Department department;

    private Integer year;

    private Role role;
}