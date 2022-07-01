package com.github.portforesearch.ecommurzbe;

import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EcommurzBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommurzBeApplication.class, args);
	}

	@Bean
	CommandLineRunner run(AuthService userService){
		return args -> {
			User user = new User();
			user.setUsername("fascal");
			user.setName("fascal");
			user.setEmail("fascalsj@gmail.com");
			user.setPassword("test1234");

			Role role = new Role();
			role.setName("ROLE_USER");

			userService.saveUser(user);
			userService.saveRole(role);
			userService.addRoleToUser(user.getUsername(), role.getName());

			User user1 = new User();
			user1.setUsername("fascalsj");
			user1.setName("fascalsj");
			user1.setEmail("fsapty@gmail.com");
			user1.setPassword("test1234");

			Role role1 = new Role();
			role1.setName("ROLE_ADMIN");

			userService.saveUser(user1);
			userService.saveRole(role1);
			userService.addRoleToUser(user1.getUsername(), role1.getName());
		};
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

}
