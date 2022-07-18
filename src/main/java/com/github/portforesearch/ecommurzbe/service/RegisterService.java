package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.model.User;

public interface RegisterService {
    UserResponseDto register(UserRequestDto userRequestDto, User user);
}
