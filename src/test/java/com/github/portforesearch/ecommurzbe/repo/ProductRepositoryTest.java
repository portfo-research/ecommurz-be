package com.github.portforesearch.ecommurzbe.repo;

import com.github.portforesearch.ecommurzbe.model.Product;
import com.github.portforesearch.ecommurzbe.specification.ProductSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.portforesearch.ecommurzbe.constant.RowStatusConstant.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepo productRepo;

    @Test
    void findAllBySpecification() {
        String sellerId = UUID.randomUUID().toString();

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setRecordStatusId(ACTIVE);
        product.setName("Search Product");
        product.setDescription("Search Product Description");

        productRepo.save(product);

        Map<String, String> filter = new HashMap<>();
        filter.put("sellerId", sellerId);
        filter.put("search", "search");

        List<Product> productList = productRepo.findAll(ProductSpecification.filter(filter));

        assertNotNull(productList.get(0).getId());
        assertEquals(product.getName(), productList.get(0).getName());
        assertEquals(product.getDescription(), productList.get(0).getDescription());
    }

}