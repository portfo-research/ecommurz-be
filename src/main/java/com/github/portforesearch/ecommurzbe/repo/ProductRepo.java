package com.github.portforesearch.ecommurzbe.repo;

import com.github.portforesearch.ecommurzbe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndRecordStatusId(String id, Integer recordStatusId);
}
