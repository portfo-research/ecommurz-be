package com.github.portforesearch.ecommurzbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Setter
@Getter
@JsonInclude(NON_EMPTY)
public class ResponseSuccess {
    Integer statusCode;
    String message;
    Object data;
}
