package com.cts.MovieBookingApp.Services;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.MovieBookingApp.Exception.MovieNotFoundException;
import com.cts.MovieBookingApp.Models.Movie;
import com.cts.MovieBookingApp.Models.Movie.MovieId;
import com.cts.MovieBookingApp.Models.Ticket;
import com.cts.MovieBookingApp.Models.User;
import com.cts.MovieBookingApp.Repository.MovieRepository;
import com.cts.MovieBookingApp.Repository.TicketRepository;
import com.cts.MovieBookingApp.Repository.UserRepository;
import com.cts.MovieBookingApp.dto.ForgotPasswordDTO;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

//	@Override
//	public User loginUser(LoginClass loginClass) {
//		List<User> users = userRepository.findAll();
//		for (User user : users) {
//			if(user.getEmail().equals(loginClass.getEmail()) && 
//					user.getPassword().equals(loginClass.getPassword())) {
//				return user;
//			}
//		}
//		return null;
//	}
//	
	//@Override
	public boolean forgetPassword(ForgotPasswordDTO request) {
		
	       Optional<User> optionalUser= userRepository.findByEmail(request.getEmail());
	       if(!optionalUser.isEmpty()) {
	    	User user=optionalUser.get();
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		    userRepository.save(user);
			return true;
		}
		return false;
	}


	@Override
	public List<Movie> searchByMovieName(String name) {
		//String movieName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		List<Movie> movies = movieRepository.findAll();
		String movieName = name.toLowerCase();
		List<Movie> moviesByName = movies.stream().filter((movie) -> 
		movie.getId().getMovieName().contains(movieName)).collect(Collectors.toList());
		return moviesByName;
	}

	@Override
	public ResponseEntity<?> deleteMovie(String movieName, String theaterName) {
		movieRepository.deleteById(new Movie.MovieId(movieName, theaterName));
		return new ResponseEntity<String>("Deleted Successfully", HttpStatus.OK);
	}

	@Override
	public boolean updateTicketRemains(MovieId key, int noOfTickets) throws MovieNotFoundException {
		Optional<Movie> movie = movieRepository.findById(key);
		if(!movie.isPresent()) {
			throw new MovieNotFoundException("Movie Not Found");
		}
		
		if(noOfTickets <= movie.get().getAllotedSeats()) {
			int count = movie.get().getAllotedSeats() - noOfTickets;
			movie.get().setAllotedSeats(count);
			movieRepository.save(movie.get());
			return true;
		}
		return false;
	}
	
//	public void updateTicketStatus(String movieName,String theatherName) {
//		List<Ticket> tickets=ticketRepository.findByMovieNameAndtheatherName(movieName,theatherName);
//		
//		int bookedTickets=tickets.stream().mapToInt(Ticket::getNoOfTickets).sum();
//		Movie.MovieId movieId=new Movie.MovieId(movieName,theatherName);
//		Optional<Movie> movieOptional=movieRepository.findById(movieId);
//		if(movieOptional.isPresent()) {
//			Movie movie=movieOptional.get();
//			int availableTickets=movie.getAllotedSeats()-bookedTickets;
//			if(availableTickets==0) {
//				movie.setStatus("SOLD OUT");
//			}
//			else
//			{
//				movie.setStatus("BOOK ASAP");
//			}	
//			movieRepository.save(movie);
//		}
//		
//	}
	
	public String addUser(User userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userRepository.save(userInfo);
        return "user added to system ";
    }
	
//	public ResponseEntity<String> updateMovieStatus(Movie movie,String movieName,String theatherName){
//		Ticket tickets =new Ticket();
//		int bookedTickets=tickets.getNoOfTickets();
//		
//		Movie.MovieId key=new Movie.MovieId(movieName, theatherName);
//	//	Movie.MovieId key = new Movie.MovieId(movie.getId().getMovieName().toLowerCase(), movie.getId().getTheatherName().toLowerCase());
//		
//		Optional<Movie> movieOptional=movieRepository.findById(key);
//		if(movieOptional.isPresent()) {
//			Movie movie1=movieOptional.get();
//			int availableTickets=movie.getAllotedSeats()-bookedTickets;
//			if(availableTickets==0) {
//			//	movie.setStatus("SOLD OUT");
//				return new ResponseEntity<String>("SOLD OUT", HttpStatus.OK);
//			}
//			else
//			{
//				//movie.setStatus("BOOK ASAP");
//				return new ResponseEntity<String>("BOOK ASAP", HttpStatus.OK);
//			}	
//			return movieRepository.save(movie);
//		}
//	}
	
//	int count=0;
//	public boolean updateTicketStatus(MovieId key, int noOfTickets) throws MovieNotFoundException {
//		Optional<Movie> movie = movieRepository.findById(key);
//		if(!movie.isPresent()) {
//			throw new MovieNotFoundException("Movie Not Found");
//		}
//		
//		if(noOfTickets <= movie.get().getAllotedSeats()) {
//			 count = movie.get().getAllotedSeats() - noOfTickets;
//			movie.get().setAllotedSeats(count);
//			movieRepository.save(movie.get());
//			return true;
//		}
//		else if(count==0) {
//			return false;
//		}
//		return false;
//	}
	
	
}

