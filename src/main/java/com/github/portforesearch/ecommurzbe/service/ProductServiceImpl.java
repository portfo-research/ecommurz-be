package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.exception.ProductNotFoundException;
import com.github.portforesearch.ecommurzbe.model.Product;
import com.github.portforesearch.ecommurzbe.repo.ProductRepo;
import com.github.portforesearch.ecommurzbe.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepository;
    private final UserService userService;

    @Override
    public Product create(Product product) {
        product.setCreatedDate(new Date());

        product.setCreatedBy(product.getSellerId());
        product.setUpdatedBy(product.getSellerId());
        product.setRecordStatusId(RowStatusConstant.ACTIVE);
        product.setUpdatedDate(new Date());
        return productRepository.save(product);
    }

    @Override
    public Boolean validateAccess(String productSellerId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return productSellerId.equals(userService.findByUsername(username).getSellerId());
    }

    @Override
    public Product findById(String id) {
        return productRepository.findByIdAndRecordStatusId(id,
                RowStatusConstant.ACTIVE).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }


    @Override
    public Product update(Product product) {
        product.setUpdatedDate(new Date());
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        product.setRecordStatusId(RowStatusConstant.DELETED);
        productRepository.save(product);
    }

    @Override
    public List<Product> findAllProductBy(Map<String, String> filter) {
        return productRepository.findAll(ProductSpecification.filter(filter));
    }
}
