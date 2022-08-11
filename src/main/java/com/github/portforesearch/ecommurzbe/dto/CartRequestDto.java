package com.github.portforesearch.ecommurzbe.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartRequestDto {
    private String customerId;
    private String productId;
    private Integer quantity;
}
