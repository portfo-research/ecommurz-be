package com.github.portforesearch.ecommurzbe.module.auth.service;

import com.github.portforesearch.ecommurzbe.module.user.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.module.user.model.User;

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
