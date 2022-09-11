package com.github.portforesearch.ecommurzbe.module.cart.dto;

import com.github.portforesearch.ecommurzbe.module.product.dto.ProductResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartResponseDto {
    private String id;
    private Integer quantity;
    private Double itemAmount;
    private Double totalAmount;
    private ProductResponse product;
}
