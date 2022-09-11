package com.github.portforesearch.ecommurzbe.module.customer;

import com.github.portforesearch.ecommurzbe.module.user.model.User;
import com.github.portforesearch.ecommurzbe.module.cart.model.Cart;
import com.github.portforesearch.ecommurzbe.model.Action;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Customer extends Action {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String firstname;
    private String lastname;
    private String fullname;
    private String address;

    @OneToOne(mappedBy = "customer", fetch = LAZY)
    private User user;

    @OneToMany(mappedBy = "customer")
    private List<Cart> carts = new ArrayList<>();

}