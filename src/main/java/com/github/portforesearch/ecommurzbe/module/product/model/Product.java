package com.github.portforesearch.ecommurzbe.module.product.model;

import com.github.portforesearch.ecommurzbe.model.Action;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Product extends Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(nullable = false)
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String name;
    private String description;
    private String image;
    private Double price;
    @Column(nullable = false)
    private int quantity;

    @Column(name = "seller_id")
    private String sellerId;

}
