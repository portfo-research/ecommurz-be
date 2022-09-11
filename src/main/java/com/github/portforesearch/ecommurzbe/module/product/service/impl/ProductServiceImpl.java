package com.github.portforesearch.ecommurzbe.module.product.service.impl;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.module.product.model.Product;
import com.github.portforesearch.ecommurzbe.module.user.model.User;
import com.github.portforesearch.ecommurzbe.module.product.repo.ProductRepo;
import com.github.portforesearch.ecommurzbe.module.product.service.ProductService;
import com.github.portforesearch.ecommurzbe.module.user.service.UserService;
import com.github.portforesearch.ecommurzbe.module.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Boolean> validateAccess(String productSellerId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByUsername(username);
        return user.map(usr -> usr.getSellerId() != null && productSellerId.equals(usr.getSellerId()));
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findByIdAndRecordStatusId(id,
                RowStatusConstant.ACTIVE);
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
