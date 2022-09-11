package com.github.portforesearch.ecommurzbe;

import com.github.portforesearch.ecommurzbe.module.auth.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EcommurzBeApplicationTests {

    @Autowired
    private AuthController userController;

    @Test
    void contextLoads() {
        EcommurzBeApplication.main(new String[]{});
        assertNotNull(userController);
    }

}
