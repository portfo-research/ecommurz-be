package com.github.portforesearch.ecommurzbe.module.user.service;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.module.cart.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.module.product.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.module.cart.model.Cart;
import com.github.portforesearch.ecommurzbe.module.cart.service.impl.CartServiceImpl;
import com.github.portforesearch.ecommurzbe.module.product.model.Product;
import com.github.portforesearch.ecommurzbe.module.product.service.impl.ProductServiceImpl;
import com.github.portforesearch.ecommurzbe.module.cart.repo.CartRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CartServiceImplTest {
    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    ProductServiceImpl productService;

    @Mock
    CartRepo cartRepo;

    ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void findAllCartByCustomerId_thenReturnSuccess() {
        //GIVEN
        String customerId = UUID.randomUUID().toString();
        when(cartRepo.findAllByCustomerIdAndRecordStatusId(anyString(), anyInt())).thenReturn(List.of(new Cart()));
        //WHEN
        List<Cart> cartList = cartService.findAllCartByCustomerId(customerId);
        //THEN

        assertNotNull(cartList);
    }

    @Test
    void addProductToCart_thenReturnSuccess() {
        //GIVEN
        String productId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setCustomerId(customerId);

        Product product = new Product();
        product.setId(productId);

        when(productService.findById(any())).thenReturn(Optional.of(product));
        when(cartRepo.save(any())).thenReturn(cart);


        //WHEN
        cartService.addProductToCart(cart);

        //THEN
        verify(cartRepo).save(cartArgumentCaptor.capture());
        Cart cartValue = cartArgumentCaptor.getValue();

        assertEquals(customerId, cartValue.getCreatedBy());
        assertEquals(customerId, cartValue.getUpdatedBy());
        assertEquals(customerId, cartValue.getCustomerId());
        assertEquals(customerId, cartValue.getCustomer().getId());
        assertEquals(productId, cartValue.getProductId());
        assertEquals(productId, cartValue.getProduct().getId());
        assertEquals(RowStatusConstant.ACTIVE, cartValue.getRecordStatusId());
        Assertions.assertNotNull(cartValue.getCreatedDate());
        Assertions.assertNotNull(cartValue.getUpdatedDate());
    }

    @Test
    void addProductToCart_thenThrowProductNotFoundException() {
        //GIVEN
        String customerId = UUID.randomUUID().toString();
        String productId = UUID.randomUUID().toString();
        Cart cart = new Cart();
        cart.setCustomerId(customerId);
        cart.setProductId(productId);

        when(productService.findById(any())).thenReturn(Optional.empty());
        //WHEN
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> cartService.addProductToCart(cart));
        assertEquals("Product not found", productNotFoundException.getMessage());
    }

    @Test
    void mapToResponse_thenReturnSuccess() {
        //GIVEN
        Product product = new Product();
        double price = 10.0;
        int quantity = 2;

        product.setPrice(price);
        Cart cart = new Cart();
        cart.setProduct(product);
        cart.setQuantity(quantity);

        Double totalAmount = price * quantity;


        //WHEN
        CartResponseDto cartResponseDto = cartService.mapToResponse(cart);

        assertEquals(totalAmount, cartResponseDto.getTotalAmount());
    }
}
