package com.github.portforesearch.ecommurzbe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
    private List<String> role;
}
