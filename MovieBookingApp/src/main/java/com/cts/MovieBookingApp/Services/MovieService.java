package com.cts.MovieBookingApp.Services;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cts.MovieBookingApp.Exception.MovieNotFoundException;
import com.cts.MovieBookingApp.Models.Movie;
import com.cts.MovieBookingApp.Models.Movie.MovieId;
import com.cts.MovieBookingApp.Models.User;
import com.cts.MovieBookingApp.dto.ForgotPasswordDTO;



@Service
public interface MovieService {
	//public User loginUser(LoginClass loginClass);
//	public boolean forgetPassword(LoginClass loginClass);
	public List<Movie> searchByMovieName(String name);
	public boolean updateTicketRemains(Movie.MovieId key, int noOfTickets) throws MovieNotFoundException;
	public ResponseEntity<?> deleteMovie(String movieName, String theaterName);
	public String addUser(User userInfo);
	//public void updateTicketStatus(String movieName, String theatherName);
	//public ResponseEntity<String> updateMovieStatus(Movie movie,String movieName,String theatherName);
	//public boolean updateTicketStatus(MovieId key, int noOfTickets) throws MovieNotFoundException;
	public boolean forgetPassword(ForgotPasswordDTO request);
}