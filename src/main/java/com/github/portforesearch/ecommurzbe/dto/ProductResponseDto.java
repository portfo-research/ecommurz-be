package com.github.portforesearch.ecommurzbe.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDto {
    private String id;
    private String name;
    private String description;
    private String image;
    private Double price;
    private Integer quantity;
    private String sellerId;
}
