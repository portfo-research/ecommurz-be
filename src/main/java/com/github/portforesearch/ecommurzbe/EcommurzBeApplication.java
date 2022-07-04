package com.github.portforesearch.ecommurzbe;

import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.AuthService;
import com.github.portforesearch.ecommurzbe.service.RoleService;
import com.github.portforesearch.ecommurzbe.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.*;

@SpringBootApplication
public class EcommurzBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommurzBeApplication.class, args);
	}

	@Bean
	CommandLineRunner run(AuthService authService, UserService userService, RoleService roleService){
		return args -> {

			Role role = new Role();

			role.setName(GENERAL_USER);
			roleService.saveRole(role);

			Role role1 = new Role();
			role1.setName(SUPER_ADMIN);
			roleService.saveRole(role1);

			Role role2 = new Role();
			role2.setName(MANAGER);
			roleService.saveRole(role2);

			User user = new User();
			user.setUsername("fascal");
			user.setEmail("fascalsj@gmail.com");
			user.setPassword("test1234");

			userService.save(user);
			authService.addRoleToUser(user.getUsername(), role.getName());
			authService.addRoleToUser(user.getUsername(), role1.getName());

			User user1 = new User();
			user1.setUsername("fascalsj");
			user1.setEmail("fsapty@gmail.com");
			user1.setPassword("test1234");

			userService.save(user1);
			authService.addRoleToUser(user1.getUsername(), role.getName());
		};
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

}
