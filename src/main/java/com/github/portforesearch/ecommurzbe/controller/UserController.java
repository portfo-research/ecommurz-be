package com.github.portforesearch.ecommurzbe.controller;

import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.mapper.UserMapper;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<User>> getAllRole(){
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping("/save")
    public ResponseEntity<User> saveRole(@RequestBody UserRequestDto userRequestDto) {
        User user = UserMapper.INSTANCE.loginRequestDtoToUser(userRequestDto);
        return ResponseEntity.ok(userService.save(user));
    }
}
