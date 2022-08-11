package com.github.portforesearch.ecommurzbe.repo;

import com.github.portforesearch.ecommurzbe.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, String> {
    List<Cart> findAllByCustomerIdAndRecordStatusId(String customerId, Integer recordStatusId);
}
