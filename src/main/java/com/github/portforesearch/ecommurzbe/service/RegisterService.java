package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.model.User;

/**
 * Registration service
 */
public interface RegisterService {
    /**
     * Register new customer
     * @param userRequestDto
     * @param user
     * @return
     */
    UserResponseDto register(UserRequestDto userRequestDto, User user);
}
