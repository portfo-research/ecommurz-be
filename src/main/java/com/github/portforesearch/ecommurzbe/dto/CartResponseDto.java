package com.github.portforesearch.ecommurzbe.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartResponseDto {
    private String id;
    private Integer quantity;
    private Double itemAmount;
    private Double totalAmount;
    private ProductResponseDto product;
}
