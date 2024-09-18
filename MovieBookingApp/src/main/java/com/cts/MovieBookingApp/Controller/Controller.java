package com.cts.MovieBookingApp.Controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cts.MovieBookingApp.Exception.MovieNotFoundException;
import com.cts.MovieBookingApp.Models.Movie;
import com.cts.MovieBookingApp.Models.Ticket;
import com.cts.MovieBookingApp.Models.User;
import com.cts.MovieBookingApp.Repository.MovieRepository;
import com.cts.MovieBookingApp.Repository.TicketRepository;
import com.cts.MovieBookingApp.Repository.UserRepository;
import com.cts.MovieBookingApp.Services.MovieService;
import com.cts.MovieBookingApp.dto.ForgotPasswordDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="*",allowedHeaders="*")
//@RequestMapping("/products")
public class Controller {
	

	private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private MovieService service;
    
    @Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	 @Autowired
	 private KafkaTemplate<String, Object> kafkaTemplate;

	 @Autowired
	 private NewTopic topic;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody User userInfo){
        return service.addUser(userInfo);
    }
    
    @GetMapping("/login")
	public ResponseEntity<?> login(@RequestHeader("Authorization")String auth) {

       String pair=new String(Base64.getDecoder().decode(auth.substring(6)));
       String password=pair.split(":")[1];
       String firstName=pair.split(":")[0];

       
        Optional<User> optionalUser= userRepository.findByFirstName(firstName);
        if(!optionalUser.isEmpty())
        {
        	User user=optionalUser.get();        	
			if(passwordEncoder.matches(password,user.getPassword()))
        	{ 	
				return new ResponseEntity<User>(user,HttpStatus.OK);
        	}
        	else {
        		return new ResponseEntity<String>("User not found", HttpStatus.OK);
        	}
        }
        return new ResponseEntity<String>("User not found", HttpStatus.OK);
    }
    
    @PostMapping("/forgotPassword")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO request){
		log.info("Inside forgot password Method");
		boolean result = movieService.forgetPassword(request);
		if(result) {
			return new ResponseEntity<String>("Password Reset Successfully", HttpStatus.OK);
		}
		log.info("Exiting forgot password Method");
		return new ResponseEntity<String>("Enter correct Email Id", HttpStatus.OK);
	}
    
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<List<Movie>> getAllMovies(){
		log.info("Inside Get All Movies Method");
		List<Movie> movies = movieRepository.findAll();
		log.info("Exiting Get All Movies Method");
		return new ResponseEntity<List<Movie>>(movies, HttpStatus.OK);
	}
    
    
    @PostMapping("/movie/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> addMovie(@RequestBody Movie movie){
		log.info("Inside Add Movie Method");
		movie.setId(new Movie.MovieId(movie.getId().getMovieName().toLowerCase(), movie.getId().getTheatherName().toLowerCase()));
		movieRepository.save(movie);
		kafkaTemplate.send(topic.name(),"Movie added by admin");
		log.info("Exiting Add Movie Method");
		return new ResponseEntity<Movie>(movie, HttpStatus.OK);
	}
    
    @GetMapping("/movies/search/{movieName}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> searchMovie(@PathVariable("movieName") String movieName){
		log.info("Inside Search Movie Method");
		List<Movie> movies = movieService.searchByMovieName(movieName);
		if(movies.size() != 0) {
			log.info("Exiting Search Movie Method");
			return new ResponseEntity<List<Movie>>(movies, HttpStatus.OK);
		}
		log.info("Exiting Search Movie Method");
		return new ResponseEntity<String>("No Movie Found", HttpStatus.OK);
	}
    
    @DeleteMapping("/{movieName}/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> deleteMovie(@PathVariable("movieName") String movieName, @PathVariable("id") String theaterName){
		log.info("Inside Delete Movie Method");
		log.info("Exiting Delete Movie Method");
		 kafkaTemplate.send(topic.name(),"Movie Deleted by the Admin. "+movieName+" is now not available");
		return movieService.deleteMovie(movieName, theaterName);
	}
    
    @PostMapping("/bookTickets")
    @PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> ticketBooking(@RequestBody Ticket ticket) throws MovieNotFoundException{
		log.info("Inside Ticket Booking App");
		Movie.MovieId key = new Movie.MovieId(ticket.getMovieName(), ticket.getTheatherName());
		if(movieService.updateTicketRemains(key, ticket.getNoOfTickets())) {
			Random rand = new Random();
			int randint = 100000 + rand.nextInt(900000);
			ticket.setTicketId(randint);
			ticketRepository.save(ticket);
			 kafkaTemplate.send(topic.name(),"ticket booked successfully by user.");
			log.info("Exiting Ticket Booking App");
			return new ResponseEntity<String>("Ticket Booked Successfully", HttpStatus.OK);
		}
		log.info("Exiting Ticket Booking App");
		return new ResponseEntity<String>("Tickets Unavailable", HttpStatus.OK);
	}
    
//    @PutMapping("/{movieName}/{theatherName}/update/ticketstatus")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<Void> updateTicketStatus(@PathVariable String movieName, @PathVariable String theatherName)
//    {
//    	movieService.updateTicketStatus(movieName,theatherName);
//		return  ResponseEntity.ok().build();
//    	
//    }
//    
//    @PutMapping("/movie/update/{movieName}/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//	public ResponseEntity<String> updateMovie(@RequestBody Movie movie,@PathVariable("movieName") String movieName,@PathVariable("id") String theatherName){
//		log.info("Inside Add UpdateMovie Method");
//				//movieRepository.save(movie);
//		movieService.updateMovieStatus(movie,movieName,theatherName);
//	//	kafkaTemplate.send(topic.name(),"Movie added by admin");
//		log.info("Exiting update Movie Method");
//		return new ResponseEntity<String>(), HttpStatus.OK);
//	}
    int count =0;
    boolean flag;
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> updateMovieTicket(@RequestBody Movie movie) throws MovieNotFoundException{
		log.info("Inside Movie Ticket Booking App");
		Ticket ticket=new Ticket();
		Movie.MovieId key = new Movie.MovieId(movie.getId().getMovieName(), movie.getId().getTheatherName());
		//if(movieService.updateTicketStatus(key, ticket.getNoOfTickets())) {
			int noOfTickets=ticket.getNoOfTickets();
			Optional<Movie> moviee = movieRepository.findById(key);
			if(!moviee.isPresent()) {
				throw new MovieNotFoundException("Movie Not Found");
			}
			
			if(noOfTickets <= moviee.get().getAllotedSeats()) {
				 count = moviee.get().getAllotedSeats() - noOfTickets;
				moviee.get().setAllotedSeats(count);
				movieRepository.save(moviee.get());
				if(count==0) {
					flag= false;
				}
				else {
					flag=true;
				}
			}
			
			if(flag) {
				return new ResponseEntity<String>("Ticket left  "+count+"  BOOK ASAP", HttpStatus.OK);
			}
			else {
				return new ResponseEntity<String>("SOLD OUT", HttpStatus.OK);
			}
			
			
	//		 kafkaTemplate.send(topic.name(),"ticket booked successfully by user.");
			//log.info("Exiting Ticket Booking App");
			
//		//}
//	//	log.info("Exiting Ticket Booking App");
//		return new ResponseEntity<String>("SOLD OUT", HttpStatus.OK);
	}
    
}


