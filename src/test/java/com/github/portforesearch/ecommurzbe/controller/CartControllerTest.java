package com.github.portforesearch.ecommurzbe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.portforesearch.ecommurzbe.dto.CartRequestDto;
import com.github.portforesearch.ecommurzbe.model.Cart;
import com.github.portforesearch.ecommurzbe.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @MockBean
    CartService cartService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void addProductToCart_thenReturnSuccess() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/cart")
                .content(generateRequestJson())
                .contentType(MediaType.APPLICATION_JSON);


        doNothing().when(cartService).addProductToCart(any());

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Product has been add into cart")));

    }

    @Test
    void getProductByCustomerId_thenReturnSuccess() throws Exception {
        String customerId = UUID.randomUUID().toString();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/cart")
                .param("customerId", customerId)
                .contentType(MediaType.APPLICATION_JSON);

        when(cartService.findAllCartByCustomerId(any())).thenReturn(List.of(new Cart()));
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(jsonPath("$.statusCode", is(200)));
        resultActions.andExpect(jsonPath("$.message", is("Cart data list")));
    }

    private String generateRequestJson() throws JsonProcessingException {

        String customerId = UUID.randomUUID().toString();
        String productId = UUID.randomUUID().toString();
        CartRequestDto cartRequestDto = new CartRequestDto();
        cartRequestDto.setQuantity(2);
        cartRequestDto.setCustomerId(customerId);
        cartRequestDto.setProductId(productId);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(cartRequestDto);
    }

}