package com.github.portforesearch.ecommurzbe.controller;

import com.github.portforesearch.ecommurzbe.common.Response;
import com.github.portforesearch.ecommurzbe.dto.ProductRequestDto;
import com.github.portforesearch.ecommurzbe.dto.ProductResponseDto;
import com.github.portforesearch.ecommurzbe.dto.ResponseSuccessDto;
import com.github.portforesearch.ecommurzbe.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.exception.UnauthorizedSellerException;
import com.github.portforesearch.ecommurzbe.mapper.ProductMapper;
import com.github.portforesearch.ecommurzbe.model.Product;
import com.github.portforesearch.ecommurzbe.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseSuccessDto> create(@RequestBody ProductRequestDto productRequestDto) {
        Product product = ProductMapper.INSTANCE.productRequestDtoToProduct(productRequestDto);
        Product savedProduct = productService.create(product);

        ProductResponseDto productResponseDto = ProductMapper.INSTANCE.productToProductResponseDto(savedProduct);
        return ResponseEntity.ok(Response.getResponseSuccessDto(productResponseDto, "Product has been" +
                " created"));
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseSuccessDto> update(@PathVariable String id,
                                                     @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productService.findById(id).map(product -> {
            String sellerId = Objects.requireNonNull(product.getSellerId(), "Product doesn't have seller");
            return productService.validateAccess(sellerId).filter(Boolean::booleanValue).map(bool -> {
                Product productMapped = ProductMapper.INSTANCE.productRequestDtoToProduct(productRequestDto);
                Product updateProductMapped = ProductMapper.INSTANCE.productToProduct(productMapped, product);
                Product updateProduct = productService.update(updateProductMapped);
                return ProductMapper.INSTANCE.productToProductResponseDto(updateProduct);
            }).orElseThrow(() -> new UnauthorizedSellerException("You don't have access to update product"));
        }).orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return ResponseEntity.ok(Response.getResponseSuccessDto(productResponseDto, "Product has " +
                "been updated"));
    }

    @GetMapping
    public ResponseEntity<ResponseSuccessDto> getProductByFilter(@RequestParam Map<String, String> filter) {
        List<Product> productList = productService.findAllProductBy(filter);
        List<ProductResponseDto> productResponseDtoList =
                productList.stream().map(ProductMapper.INSTANCE::productToProductResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(Response.getResponseSuccessDto(productResponseDtoList, "Product " +
                "List"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseSuccessDto> delete(@PathVariable String id) {
        String message = productService.findById(id).map(product -> {
                    String sellerId = Objects.requireNonNull(product.getSellerId());
                    String deleteProductMessage = "Product has been deleted";
                    return productService.validateAccess(sellerId).filter(Boolean::booleanValue).map(bool ->
                            deleteProductMessage);
                }).orElseThrow(() -> new UnauthorizedSellerException("You don't have access to update product"))
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return ResponseEntity.ok(Response.getResponseSuccessDto(null, message));
    }
}
