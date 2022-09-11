package com.github.portforesearch.ecommurzbe.module.product.specification;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.module.product.model.Product;
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

                //Filter by seller Id
                String sellerIdKey = "sellerId";
                Object sellerId = filter.get(sellerIdKey);
                if (sellerId != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(product.get("sellerId"),
                            sellerId.toString()));
                }

                //Filter by product and description
                Object search = filter.get("search");
                if (search != null) {
                    predicate = criteriaBuilder.and(predicate, search(product, criteriaBuilder,
                            search.toString()));
                }

                return predicate;
            }
        };

    }

    /**
     * Filter by product name and description
     * @param product
     * @param criteriaBuilder
     * @param text
     * @return
     */
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
