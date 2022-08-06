package com.github.portforesearch.ecommurzbe.controller;

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
import java.util.Optional;
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
        productResponseDto.setSellerId(product.getSellerId());
        return ResponseEntity.ok(getResponseSuccessDto(productResponseDto, "Product has been created"));
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseSuccessDto> update(@PathVariable String id,
                                                     @RequestBody ProductRequestDto productRequestDto) {
        productService.findById(id).map(
                product -> {
                    String sellerId = Objects.requireNonNull(product.getSellerId());
                    Optional<Boolean> validateAccess = productService.validateAccess(sellerId);
                    if (validateAccess.isPresent() && Boolean.TRUE.equals(validateAccess.get())) {
                        Product productMapped = ProductMapper.INSTANCE.productRequestDtoToProduct(productRequestDto);
                        Product updateProductMapped = ProductMapper.INSTANCE.productToProduct(productMapped, product);
                        Product updateProduct = productService.update(updateProductMapped);
                        ProductResponseDto productResponseDto =
                                ProductMapper.INSTANCE.productToProductResponseDto(updateProduct);
                        return ResponseEntity.ok(getResponseSuccessDto(productResponseDto, "Product has been updated"));
                    }
                    throw new UnauthorizedSellerException("You don't have access to update product");
                }
        );
        throw new UnauthorizedSellerException("You don't have access to update product");
    }

    @GetMapping
    public ResponseEntity<ResponseSuccessDto> getProductByFilter(@RequestParam Map<String, String> filter) {
        List<Product> productList = productService.findAllProductBy(filter);
        List<ProductResponseDto> productResponseDtoList =
                productList.stream().map(ProductMapper.INSTANCE::productToProductResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(getResponseSuccessDto(productResponseDtoList, "Product List"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseSuccessDto> delete(@PathVariable String id) {
        productService.findById(id).map(product -> {
            Optional<Boolean> validateAccess = productService.validateAccess(product.getSellerId());
            if (validateAccess.isPresent() && Boolean.TRUE.equals(validateAccess.get())) {
                productService.delete(product);
                return ResponseEntity.ok(getResponseSuccessDto(null, "Product has been deleted"));
            }
            throw new ProductNotFoundException("You don't have access to delete");
        });
        throw new ProductNotFoundException("You don't have access to delete");
    }


    private ResponseSuccessDto getResponseSuccessDto(Object data, String message) {
        ResponseSuccessDto responseSuccessDto = new ResponseSuccessDto();
        responseSuccessDto.setMessage(message);
        responseSuccessDto.setStatusCode(200);
        responseSuccessDto.setData(data);
        return responseSuccessDto;
    }

}
