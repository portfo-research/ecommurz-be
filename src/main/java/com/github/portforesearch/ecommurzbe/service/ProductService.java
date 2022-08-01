package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Product create(Product product);

    Boolean validateAccess(String productSellerId);

    Product findById(String id);

    Product update(Product product);

    void delete(Product product);

    List<Product> findAllProductBy(Map<String,String> filter);
}
