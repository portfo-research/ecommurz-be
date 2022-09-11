package com.github.portforesearch.ecommurzbe.module.cart.service.impl;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.module.cart.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.module.product.dto.ProductResponse;
import com.github.portforesearch.ecommurzbe.module.product.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.module.cart.mapper.CartMapper;
import com.github.portforesearch.ecommurzbe.module.product.mapper.ProductMapper;
import com.github.portforesearch.ecommurzbe.module.cart.model.Cart;
import com.github.portforesearch.ecommurzbe.module.customer.Customer;
import com.github.portforesearch.ecommurzbe.module.product.model.Product;
import com.github.portforesearch.ecommurzbe.module.product.service.ProductService;
import com.github.portforesearch.ecommurzbe.module.cart.repo.CartRepo;
import com.github.portforesearch.ecommurzbe.module.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final ProductService productService;
    private final CartRepo cartRepo;


    @Override
    public List<Cart> findAllCartByCustomerId(String customerId) {
        return cartRepo.findAllByCustomerIdAndRecordStatusId(customerId, RowStatusConstant.ACTIVE);
    }

    @Override
    public void addProductToCart(Cart cart) throws ProductNotFoundException {
        String productId = Objects.requireNonNull(cart.getProductId());
        String customerId = Objects.requireNonNull(cart.getCustomerId());

        Product product = productService.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product " +
                "not found"));

        Customer customer = new Customer();
        customer.setId(customerId);
        cart.setCreatedBy(cart.getCustomerId());
        cart.setUpdatedBy(cart.getCustomerId());
        cart.setCreatedDate(new Date());
        cart.setUpdatedDate(new Date());
        cart.setRecordStatusId(RowStatusConstant.ACTIVE);
        cart.setProduct(product);
        cart.setCustomer(customer);
        cartRepo.save(cart);
        log.info("Product has been add to card", cart);

    }

    @Override
    public CartResponseDto mapToResponse(Cart cart) {
        ProductResponse productResponse = ProductMapper.INSTANCE.productToProductResponseDto(cart.getProduct());

        Double itemAmount = productResponse.getPrice();
        Double totalAmount = itemAmount * cart.getQuantity().doubleValue();

        CartResponseDto cartResponseDto = CartMapper.INSTANCE.cartToCartDto(cart);
        cartResponseDto.setProduct(productResponse);
        cartResponseDto.setItemAmount(itemAmount);

        cartResponseDto.setTotalAmount(totalAmount);
        return cartResponseDto;
    }
}
