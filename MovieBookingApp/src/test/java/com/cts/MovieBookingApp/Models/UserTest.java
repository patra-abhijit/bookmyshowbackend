package com.cts.MovieBookingApp.Models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
	
	User user1 = new User();
	
	@Test
	@DisplayName("Checking If User Class Loading or not")
	void userIsLoadingOrNot() {
		assertThat(user1).isNotNull();
	}
	
	@Test
	@DisplayName("Cheking If User Class constructor responding correctly or not.")
	void testUserClass() {
		User user = new User(101, "Sid", "Thorat", "sid@gmail.com", "pwd1", "9359479510","Admin");
		
		assertEquals(101, user.getLoginId());
		assertEquals("Sid", user.getFirstName());
		assertEquals("Thorat", user.getLastName());
		assertEquals("sid@gmail.com", user.getEmail());
		assertEquals("pwd1", user.getPassword());
		assertEquals("9359479510", user.getContactNumber());
		assertEquals("Admin", user.getRoles());
	}
	
	@Test
	@DisplayName("Cheking If User Class Getter and Setter responding correctly or not.")
	void testGetterSetterofUserClass() {
		User user = new User();
		
		user.setLoginId(101);
		user.setFirstName("Sid");
		user.setLastName("Thorat");
		user.setEmail("sid@gmail.com");
		user.setPassword("pwd1");
		user.setContactNumber("9359479510");
		user.setRoles("Admin");
		assertEquals(101, user.getLoginId());
		assertEquals("Sid", user.getFirstName());
		assertEquals("Thorat", user.getLastName());
		assertEquals("sid@gmail.com", user.getEmail());
		assertEquals("pwd1", user.getPassword());
		assertEquals("9359479510", user.getContactNumber());
		assertEquals("Admin", user.getRoles());
		
	}
}