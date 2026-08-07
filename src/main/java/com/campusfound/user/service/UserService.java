package com.campusfound.user.service;

import com.campusfound.user.dto.UpdateProfileRequest;
import com.campusfound.user.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse updateProfile(UpdateProfileRequest request);

}