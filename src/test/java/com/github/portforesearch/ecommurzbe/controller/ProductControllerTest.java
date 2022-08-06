package com.github.portforesearch.ecommurzbe.controller;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.portforesearch.ecommurzbe.common.Token;
import com.github.portforesearch.ecommurzbe.dto.ProductRequestDto;
import com.github.portforesearch.ecommurzbe.model.Product;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    public static final String PRODUCT_NAME = "Barang Bekas";
    public static final String DESCRIPTION = "Barang Bekas Description";
    public static final double PRICE = 5000.56;
    public static final String IMAGE = "http://image.images.jpg";
    public static final int QUANTITY = 1;
    private final String USERNAME = "username";
    private final String sellerId = UUID.randomUUID().toString();
    private final String productId = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ProductService productService;

    private String generateRequestJson() throws JsonProcessingException {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setName(PRODUCT_NAME);
        productRequestDto.setSellerId(sellerId);
        productRequestDto.setDescription(DESCRIPTION);
        productRequestDto.setPrice(PRICE);
        productRequestDto.setQuantity(QUANTITY);
        productRequestDto.setImage(IMAGE);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(productRequestDto);

    }

    private Product generateProduct() {
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setName(PRODUCT_NAME);
        product.setDescription(DESCRIPTION);
        product.setPrice(PRICE);
        product.setQuantity(QUANTITY);
        product.setImage(IMAGE);
        return product;
    }

    private String generateToken() {
        User user = new User();
        user.setUsername(USERNAME);

        Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());

        String token = Token.generate(algorithm, user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), 60, "/api/test");
        return token;
    }

    @Test
    void createProductSuccess() throws Exception {
        Product product = generateProduct();
        when(productService.create(any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/product")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON)

                .header(AUTHORIZATION, "Bearer " + generateToken());

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product has been created")));
        resultActions.andExpect(jsonPath("$.data.id", is(productId)));
        resultActions.andExpect(jsonPath("$.data.name", is(product.getName())));
        resultActions.andExpect(jsonPath("$.data.description", is(product.getDescription())));
        resultActions.andExpect(jsonPath("$.data.image", is(product.getImage())));
        resultActions.andExpect(jsonPath("$.data.quantity", is(product.getQuantity())));
        resultActions.andExpect(jsonPath("$.data.price", is(product.getPrice())));
    }


    @Test
    void updateProductSuccess() throws Exception {
        Product product = generateProduct();
        when(productService.findById(anyString())).thenReturn(product);
        when(productService.validateAccess(anyString())).thenReturn(true);
        when(productService.update(any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + generateToken());

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product has been updated")));
        resultActions.andExpect(jsonPath("$.data.id", is(productId)));
        resultActions.andExpect(jsonPath("$.data.name", is(product.getName())));
        resultActions.andExpect(jsonPath("$.data.description", is(product.getDescription())));
        resultActions.andExpect(jsonPath("$.data.image", is(product.getImage())));
        resultActions.andExpect(jsonPath("$.data.quantity", is(product.getQuantity())));
        resultActions.andExpect(jsonPath("$.data.price", is(product.getPrice())));
    }

    @Test
    void updateProductThrowWhenProductNull() throws Exception {

        Product product = generateProduct();
        when(productService.findById(anyString())).thenReturn(null);
        when(productService.validateAccess(anyString())).thenReturn(true);
        when(productService.update(any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + generateToken());

        AuthorizationServiceException customAuthorizationFilter =
                Assertions.assertThrows(AuthorizationServiceException.class, () -> mockMvc.perform(requestBuilder));

        assertEquals("Request processing failed; nested exception is com.github.portforesearch.ecommurzbe.exception" +
                ".UnauthorizedSellerException: You don't have access to update product",
                customAuthorizationFilter.getMessage());
    }

    @Test
    void getProductByFilterSuccess() throws Exception {
        Product product = generateProduct();
        String sellerId = "78603a0e-c636-4fb8-9372-19b8cffe9cec";
        Map<String, String> filter = new HashMap<>();
        filter.put("sellerId", sellerId);
        filter.put("search", "search");

        when(productService.findAllProductBy(filter)).thenReturn(Collections.singletonList(product));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/product")
                .content(generateRequestJson())
                .param("sellerId", sellerId)
                .param("search", "search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + generateToken());

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product List")));
        resultActions.andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void deleteProductSuccess() throws Exception {
        Product product = generateProduct();
        when(productService.findById(anyString())).thenReturn(product);
        when(productService.validateAccess(anyString())).thenReturn(true);
        doNothing().when(productService).delete(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + generateToken());

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product has been deleted")));
    }
}