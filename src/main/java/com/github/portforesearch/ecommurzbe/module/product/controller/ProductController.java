package com.github.portforesearch.ecommurzbe.module.product.controller;

import com.github.portforesearch.ecommurzbe.dto.Response;
import com.github.portforesearch.ecommurzbe.module.product.dto.ProductRequest;
import com.github.portforesearch.ecommurzbe.module.product.dto.ProductResponse;
import com.github.portforesearch.ecommurzbe.dto.ResponseSuccess;
import com.github.portforesearch.ecommurzbe.module.product.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.module.auth.exception.UnauthorizedSellerException;
import com.github.portforesearch.ecommurzbe.module.product.mapper.ProductMapper;
import com.github.portforesearch.ecommurzbe.module.product.model.Product;
import com.github.portforesearch.ecommurzbe.module.product.service.ProductService;
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
    public ResponseEntity<ResponseSuccess> create(@RequestBody ProductRequest productRequest) {
        Product product = ProductMapper.INSTANCE.productRequestDtoToProduct(productRequest);
        Product savedProduct = productService.create(product);

        ProductResponse productResponse = ProductMapper.INSTANCE.productToProductResponseDto(savedProduct);
        return ResponseEntity.ok(Response.getResponseSuccessDto(productResponse, "Product has been" +
                " created"));
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseSuccess> update(@PathVariable String id,
                                                  @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.findById(id).map(product -> {
            String sellerId = Objects.requireNonNull(product.getSellerId(), "Product doesn't have seller");
            return productService.validateAccess(sellerId).filter(Boolean::booleanValue).map(bool -> {
                Product productMapped = ProductMapper.INSTANCE.productRequestDtoToProduct(productRequest);
                Product updateProductMapped = ProductMapper.INSTANCE.productToProduct(productMapped, product);
                Product updateProduct = productService.update(updateProductMapped);
                return ProductMapper.INSTANCE.productToProductResponseDto(updateProduct);
            }).orElseThrow(() -> new UnauthorizedSellerException("You don't have access to update product"));
        }).orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return ResponseEntity.ok(Response.getResponseSuccessDto(productResponse, "Product has " +
                "been updated"));
    }

    @GetMapping
    public ResponseEntity<ResponseSuccess> getProductByFilter(@RequestParam Map<String, String> filter) {
        List<Product> productList = productService.findAllProductBy(filter);
        List<ProductResponse> productResponseList =
                productList.stream().map(ProductMapper.INSTANCE::productToProductResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(Response.getResponseSuccessDto(productResponseList, "Product " +
                "List"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseSuccess> delete(@PathVariable String id) {
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
