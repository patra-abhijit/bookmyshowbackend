package com.cts.MovieBookingApp.Repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cts.MovieBookingApp.Models.User;


@Repository
public interface UserRepository extends MongoRepository<User, Integer>{
	//public User findByEmail(String email);
	Optional<User> findByFirstName(String firstName);

	Optional<User> findByEmail(String email);

}