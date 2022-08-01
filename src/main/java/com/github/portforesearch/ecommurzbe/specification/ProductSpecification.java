package com.github.portforesearch.ecommurzbe.specification;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.model.Product;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ProductSpecification {

    public static Specification<Product> filter(Map<String, String> filter) {
        return new Specification<>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Product> product, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {

                Predicate predicate = criteriaBuilder.equal(product.get("recordStatusId"), RowStatusConstant.ACTIVE);

                Object sellerId = filter.get("sellerId");
                if (sellerId != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(product.get("sellerId"),
                            sellerId.toString()));
                }

                Object search = filter.get("search");
                if (search != null) {
                    predicate = criteriaBuilder.and(predicate, search(product, criteriaBuilder,
                            search.toString()));
                }

                return predicate;
            }
        };

    }

    private static Predicate search(Root<Product> product, CriteriaBuilder criteriaBuilder, String text) {

        return criteriaBuilder.or(
                criteriaBuilder.like(
                        criteriaBuilder.lower(product.get("name")), "%" + text.toLowerCase() + "%"
                ),
                criteriaBuilder.like(
                        criteriaBuilder.lower(product.get("description")), "%" + text.toLowerCase() + "%"
                ));

    }


}
