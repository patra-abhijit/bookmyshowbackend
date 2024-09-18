package com.cts.MovieBookingApp.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cts.MovieBookingApp.Models.Movie;


@Repository
public interface MovieRepository extends MongoRepository<Movie, Movie.MovieId> {

	

	Optional<Movie> findById(Movie.MovieId movieId);



	
	
}
