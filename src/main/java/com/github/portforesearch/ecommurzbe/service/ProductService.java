package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    /**
     * Create new product
     *
     * @param product
     * @return
     */
    Product create(Product product);

    /**
     * Validate product by seller
     *
     * @param productSellerId
     * @return
     */
    Optional<Boolean> validateAccess(String productSellerId);

    /**
     * Find product by Id
     * @param id
     * @return
     */
    Optional<Product> findById(String id);

    /**
     * Update product
     * @param product
     * @return
     */
    Product update(Product product);

    /**
     * Soft delete product
     * @param product
     */
    void delete(Product product);

    /**
     * Find product by dynamic filter
     * @param filter
     * @return
     */
    List<Product> findAllProductBy(Map<String,String> filter);
}
