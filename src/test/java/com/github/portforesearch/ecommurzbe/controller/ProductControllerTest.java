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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    public static final String PRODUCT_NAME = "Barang Bekas";
    public static final String DESCRIPTION = "Barang Bekas Description";
    public static final double PRICE = 5000.56;
    public static final String IMAGE = "http://image.images.jpg";
    public static final int QUANTITY = 1;
    public static final String EXCEPTION = "Request processing failed; nested exception is com.github.portforesearch" +
            ".ecommurzbe.exception";
    private final String sellerId = UUID.randomUUID().toString();
    private final String productId = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

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
        user.setUsername("username");

        Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());

        return Token.generate(algorithm, user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), 60, "/api/test");
    }

    @Test
    void createProduct_thenReturnSuccess() throws Exception {
        Product product = generateProduct();
        when(productService.create(ArgumentMatchers.any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/product")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

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
    void updateProduct_thenReturnSuccess() throws Exception {
        Product product = generateProduct();
        when(productService.findById(anyString())).thenReturn(Optional.ofNullable(product));
        when(productService.validateAccess(anyString())).thenReturn(Optional.of(true));
        when(productService.update(ArgumentMatchers.any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

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
    void updateProduct_whenSellerIdNull_thenThrowNestedServletException() throws Exception {
        Product product = generateProduct();
        //set seller id to null then throw NullPointerException
        product.setSellerId(null);
        when(productService.findById(anyString())).thenReturn(Optional.ofNullable(product));
        when(productService.validateAccess(anyString())).thenReturn(Optional.of(true));
        when(productService.update(ArgumentMatchers.any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

        NestedServletException nullPointerException = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(requestBuilder));

        assertEquals("Request processing failed; nested exception is java.lang.NullPointerException: Product doesn't " +
                "have seller", nullPointerException.getMessage());
    }


    @Test
    void updateProduct_whenAccessNotValidated_thenThrowNestedServletException() throws Exception {
        Product product = generateProduct();
        when(productService.findById(anyString())).thenReturn(Optional.ofNullable(product));
        //Set validate to false then throw UnauthorizedSellerException
        when(productService.validateAccess(anyString())).thenReturn(Optional.of(false));
        when(productService.update(ArgumentMatchers.any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

        NestedServletException nullPointerException = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(requestBuilder));

        assertEquals(EXCEPTION +
                        ".UnauthorizedSellerException: You don't have access to update product",
                nullPointerException.getMessage());
    }

    @Test
    void updateProduct_whenProductNotFound_thenThrowNestedServletException() throws Exception {
        //Set product to empty then throw ProductNotFoundException
        when(productService.findById(anyString())).thenReturn(Optional.empty());
        when(productService.validateAccess(anyString())).thenReturn(Optional.of(false));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

        NestedServletException nullPointerException = assertThrows(NestedServletException.class,
                () -> mockMvc.perform(requestBuilder));

        assertEquals(EXCEPTION + ".ProductNotFoundException: Product not found",
                nullPointerException.getMessage());
    }

    @Test
    void updateProduct_whenProductNull_thenThrowAuthorizationServiceException() throws Exception {
        Product product = generateProduct();
        Optional<Product> productOptional = Optional.of(product);
        when(productService.findById(anyString())).thenReturn(productOptional);
        when(productService.validateAccess(anyString())).thenReturn(Optional.of(false));
        when(productService.update(ArgumentMatchers.any(Product.class))).thenReturn(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

        NestedServletException customAuthorizationFilter =
                assertThrows(NestedServletException.class, () -> mockMvc.perform(requestBuilder));

        assertEquals(EXCEPTION +
                        ".UnauthorizedSellerException: You don't have access to update product",
                customAuthorizationFilter.getMessage());
    }

    @Test
    void getProductByFilter_thenReturnSuccess() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product List")));
        resultActions.andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void delete_thenReturnSuccess() throws Exception {
        Product product = generateProduct();
        when(productService.findById(anyString())).thenReturn(Optional.of(product));
        when(productService.validateAccess(anyString())).thenReturn(Optional.of(true));
        doNothing().when(productService).delete(product);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/product/053ce437-fe05-45e5-b59d-f66c0ec7f9f0")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product has been deleted")));
    }

}