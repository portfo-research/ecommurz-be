package com.github.portforesearch.ecommurzbe.module.cart.mapper;

import com.github.portforesearch.ecommurzbe.module.cart.dto.CartRequestDto;
import com.github.portforesearch.ecommurzbe.module.cart.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.module.cart.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);
    @Mapping(ignore = true, target = "product")
    CartResponseDto cartToCartDto(Cart cart);
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "createdDate")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "updatedDate")
    @Mapping(ignore = true, target = "recordStatusId")
    @Mapping(ignore = true, target = "customer")
    @Mapping(ignore = true, target = "product")
    Cart cartRequestDtoToCart(CartRequestDto cartRequestDto);
}
