package com.github.portforesearch.ecommurzbe.service;


import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.model.Product;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.ProductRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ProductServiceImplTest {
    public static final String SELLER_ID = UUID.randomUUID().toString();
    public static final String ID = UUID.randomUUID().toString();
    public static final String NAME = "Macbook Pro 2020 M1 Grey";
    public static final double PRICE = 1800000.0;
    public static final String DESCRIPTION = "Macbook Pro 2020 M1 Description";
    public static final String IMAGE_URL = "http://image.images.jpg";
    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    ProductRepo productRepository;

    ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

    @Mock
    private Authentication auth;
    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        openMocks(this);
        when(auth.getCredentials()).thenReturn("mockedPassword");
        when(auth.getName()).thenReturn("mockedName");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    private Product generateProduct() {
        Date date = new Date();
        Product product = new Product();
        product.setName(NAME);
        product.setPrice(PRICE);
        product.setDescription(DESCRIPTION);
        product.setImage(IMAGE_URL);
        product.setCreatedBy(SELLER_ID);
        product.setUpdatedBy(SELLER_ID);
        product.setRecordStatusId(RowStatusConstant.ACTIVE);
        product.setCreatedDate(date);
        return product;
    }

    @Test
    void createSuccess() {
        //GIVEN
        Product product = generateProduct();

        product.setId(UUID.randomUUID().toString());
        product.setSellerId(SELLER_ID);
        product.setCreatedBy(SELLER_ID);
        product.setUpdatedBy(SELLER_ID);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        //WHEN
        Product createdProduct = productService.create(product);

        //THEN
        verify(productRepository).save(productArgumentCaptor.capture());
        Product productCaptorValue = productArgumentCaptor.getValue();

        assertNotNull(createdProduct);
        assertEquals(SELLER_ID, productCaptorValue.getCreatedBy());
        assertEquals(SELLER_ID, productCaptorValue.getUpdatedBy());
        assertNotNull(productCaptorValue.getCreatedDate());
        assertNotNull(productCaptorValue.getUpdatedDate());
        assertEquals(product.getName(), createdProduct.getName());
        assertEquals(product.getImage(), createdProduct.getImage());
        assertEquals(product.getPrice(), createdProduct.getPrice());
        assertEquals(product.getDescription(), createdProduct.getDescription());
        assertEquals(product.getQuantity(), createdProduct.getQuantity());
    }


    @Test
    void updateSuccess() {
        //GIVEN
        Product product = generateProduct();
        product.setId(ID);
        product.setQuantity(20);

        when(productRepository.findByIdAndRecordStatusId(anyString(),
                anyInt())).thenReturn(java.util.Optional.of(product));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        //WHEN
        Product updatedProduct = productService.update(product);

        //THEN
        verify(productRepository).save(productArgumentCaptor.capture());
        Product productCaptorValue = productArgumentCaptor.getValue();

        assertNotNull(updatedProduct);
        assertEquals(20, productCaptorValue.getQuantity());
    }


    @Test
    void validateAccessSuccess() {
        //GIVEN
        String productSellerId = UUID.randomUUID().toString();
        User user = new User();
        user.setSellerId(productSellerId);
        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));

        //WHEN
        Optional<Boolean> result = productService.validateAccess(productSellerId);

        //THEN
        assertTrue(result.get());
    }

    @Test
    void findByIdSuccess() {
        //GIVEN
        when(productRepository.findByIdAndRecordStatusId(anyString(), anyInt())).thenReturn(Optional.of(new Product()));
        //WHEN
        Product productFound = productService.findById(UUID.randomUUID().toString());

        //THEN
        assertNotNull(productFound);
    }

    @Test
    void findByIdThrowProductNotFoundException() {
        //GIVEN
        String id = UUID.randomUUID().toString();
        when(productRepository.findByIdAndRecordStatusId(anyString(), anyInt())).thenReturn(Optional.empty());

        //WHEN
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> productService.findById(id));

        //THEN
        assertEquals("Product not found", productNotFoundException.getMessage());
    }

    @Test
    void findAllProductByFilterSuccess() {
        //GIVEN
        String sellerId = "78603a0e-c636-4fb8-9372-19b8cffe9cec";
        Map<String, String> filter = new HashMap<>();
        filter.put("sellerId", sellerId);
        filter.put("search", "search");

        when(productRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(new Product()));
        //WHEN
        List<Product> productList = productService.findAllProductBy(filter);

        //THEN
        assertNotNull(productList);
    }
}
