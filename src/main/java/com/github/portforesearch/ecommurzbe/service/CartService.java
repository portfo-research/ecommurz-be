package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.model.Cart;

import java.util.List;

public interface CartService {
    /**
     * Show all product in the card of customer
     * @param customerId
     * @return
     */
    List<Cart> findAllCartByCustomerId(String customerId);

    /**
     * Add new product in the card of customer
     * @param cart
     */
    void addProductToCart(Cart cart);

    /**
     * Map cart entity to CartDto
     * @param cart
     * @return
     */
    CartResponseDto mapToResponse(Cart cart);
}
