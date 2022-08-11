package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.dto.CartResponseDto;
import com.github.portforesearch.ecommurzbe.dto.ProductResponseDto;
import com.github.portforesearch.ecommurzbe.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.mapper.CartMapper;
import com.github.portforesearch.ecommurzbe.mapper.ProductMapper;
import com.github.portforesearch.ecommurzbe.model.Cart;
import com.github.portforesearch.ecommurzbe.model.Customer;
import com.github.portforesearch.ecommurzbe.model.Product;
import com.github.portforesearch.ecommurzbe.repo.CartRepo;
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
        ProductResponseDto productResponseDto = ProductMapper.INSTANCE.productToProductResponseDto(cart.getProduct());

        Double itemAmount = productResponseDto.getPrice();
        Double totalAmount = itemAmount * cart.getQuantity().doubleValue();

        CartResponseDto cartResponseDto = CartMapper.INSTANCE.cartToCartDto(cart);
        cartResponseDto.setProduct(productResponseDto);
        cartResponseDto.setItemAmount(itemAmount);

        cartResponseDto.setTotalAmount(totalAmount);
        return cartResponseDto;
    }
}
