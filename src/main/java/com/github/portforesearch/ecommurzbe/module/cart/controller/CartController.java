package com.github.portforesearch.ecommurzbe.module.cart.controller;

import com.github.portforesearch.ecommurzbe.dto.Response;
import com.github.portforesearch.ecommurzbe.module.cart.dto.CartRequestDto;
import com.github.portforesearch.ecommurzbe.module.cart.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.dto.ResponseSuccess;
import com.github.portforesearch.ecommurzbe.module.cart.mapper.CartMapper;
import com.github.portforesearch.ecommurzbe.module.cart.model.Cart;
import com.github.portforesearch.ecommurzbe.module.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ResponseSuccess> getProductByCustomerId(@RequestParam String customerId) {
        List<CartResponseDto> cartResponseDtos =
                cartService.findAllCartByCustomerId(customerId).stream().map(cartService::mapToResponse
                ).collect(Collectors.toList());
        return ResponseEntity.ok(Response
                .getResponseSuccessDto(cartResponseDtos, "Cart data list"));
    }


    @PostMapping
    public ResponseEntity<ResponseSuccess> addProductToCard(@RequestBody CartRequestDto cartRequestDto) {
        Cart cart = CartMapper.INSTANCE.cartRequestDtoToCart(cartRequestDto);
        cartService.addProductToCart(cart);
        return ResponseEntity.ok(Response
                .getResponseSuccessDto("Product has been add into cart"));
    }
}
