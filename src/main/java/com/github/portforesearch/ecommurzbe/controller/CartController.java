package com.github.portforesearch.ecommurzbe.controller;

import com.github.portforesearch.ecommurzbe.common.Response;
import com.github.portforesearch.ecommurzbe.dto.CartRequestDto;
import com.github.portforesearch.ecommurzbe.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.dto.ResponseSuccessDto;
import com.github.portforesearch.ecommurzbe.mapper.CartMapper;
import com.github.portforesearch.ecommurzbe.model.Cart;
import com.github.portforesearch.ecommurzbe.service.CartService;
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
    public ResponseEntity<ResponseSuccessDto> getProductByCustomerId(@RequestParam String customerId) {
        List<CartResponseDto> cartResponseDtos =
                cartService.findAllCartByCustomerId(customerId).stream().map(cartService::mapToResponse
                ).collect(Collectors.toList());
        return ResponseEntity.ok(Response
                .getResponseSuccessDto(cartResponseDtos, "Cart data list"));
    }


    @PostMapping
    public ResponseEntity<ResponseSuccessDto> addProductToCard(@RequestBody CartRequestDto cartRequestDto) {
        Cart cart = CartMapper.INSTANCE.cartRequestDtoToCart(cartRequestDto);
        cartService.addProductToCart(cart);
        return ResponseEntity.ok(Response
                .getResponseSuccessDto("Product has been add into cart"));
    }
}
