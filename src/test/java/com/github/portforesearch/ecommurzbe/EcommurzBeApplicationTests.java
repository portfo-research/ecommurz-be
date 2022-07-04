package com.github.portforesearch.ecommurzbe;

import com.github.portforesearch.ecommurzbe.controller.UserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcommurzBeApplicationTests {

	@Autowired
	private UserController userController;

	@Test
	void contextLoads() {
		EcommurzBeApplication.main(new String[]{});
		Assertions.assertNotNull(userController);
	}

}
