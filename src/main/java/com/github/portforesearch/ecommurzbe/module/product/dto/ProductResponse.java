package com.github.portforesearch.ecommurzbe.module.product.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private Double price;
    private Integer quantity;
}
