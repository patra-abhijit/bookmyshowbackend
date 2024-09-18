package com.cts.MovieBookingApp.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cts.MovieBookingApp.Exception.MovieNotFoundException;
import com.cts.MovieBookingApp.Models.Movie;
import com.cts.MovieBookingApp.Models.Ticket;
import com.cts.MovieBookingApp.Models.User;
import com.cts.MovieBookingApp.Repository.MovieRepository;
import com.cts.MovieBookingApp.Repository.TicketRepository;
import com.cts.MovieBookingApp.Repository.UserRepository;
import com.cts.MovieBookingApp.Services.MovieService;
import com.cts.MovieBookingApp.dto.ForgotPasswordDTO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
public class ControllerTest {
	
	
	protected MockMvc mvc;
	
	@MockBean
	private MovieRepository movieRepository;
	
	@MockBean
	private TicketRepository ticketRepository;
	
	@MockBean
	private MovieService service;
	
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@MockBean
	private BCryptPasswordEncoder passwordEncoder;
	
//	 @Autowired
//	 private KafkaTemplate<String, Object> kafkaTemplate;
//
//	 @Autowired
//	 private NewTopic topic;
	 
//	 @InjectMocks
//	 private Controller controller;
//	 
//	 @Autowired
//	 private ObjectMapper objectMapper;
	 
	 
	 protected void setUp() {
			mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		}

		@BeforeAll
		public void before() {
			setUp();
		}
		@BeforeEach
		public void setupSecurityContext() {
		    // Create a mock UserDetails object with the necessary roles
		    UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("admin")
		            .password("password")
		            .roles("ADMIN")
		            .build();

		    // Create an authentication token with the UserDetails object
		    UsernamePasswordAuthenticationToken authentication = 
		            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		    // Set the authentication in the SecurityContext
		    SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		protected String mapToJson(Object object) throws JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(object);
		}

		protected <T> T mapFromJson(String json, Class<T> clazz)
				throws JsonParseException, JsonMappingException, IOException {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, clazz);
		}
	 
	 @Test
	 public void testWelcome()throws Exception
	 {
		 mvc.perform(get("/welcome")).andExpect(status().isOk()).andExpect(content().string("Welcome this endpoint is not secure"));
	 }
	 
//	 @Test
//	 public void testAddNewUser()throws Exception
//	 {
// 			User user = new User(2135440, "Siddhant", "Thorat", "siddhant.thorat@gmail.com", "pwd1", "9359479510","Admin");
//		 
//		 when(movieService.addUser(user)).thenReturn("User Registered Succesfully");
//		 mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
//		 .andExpect(content().string("User Registered Succesfully"));
//	 }
	
	 @Test
		@DisplayName("Testing Register User Controller")
		void testRegisterUser() throws Exception {
		 User user = new User(2135440, "Siddhant", "Thorat", "siddhant.thorat@gmail.com", "pwd1", "9359479510","Admin");
			String url = "/register";
			String body = mapToJson(user);
			when(service.addUser(Mockito.any(User.class))).thenReturn("user added to system");
			MvcResult mvcResult = mvc
					.perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
					.andReturn();
		

			String response = mvcResult.getResponse().getContentAsString();
			System.out.println("Actual Response:"+response);
			assertEquals("user added to system", response);

			int status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
		}
	
	 	@Test
		@DisplayName("Testing Login User Controller")
		void testLoginUser() throws Exception {
			String url = "/login";
			//String credentials=Base64.getEncoder().encodeToString("sid@gmail.com".getBytes());
			String authHeader="Basic "+Base64.getEncoder().encodeToString("Siddhant:pwd1".getBytes());
			
			User user = new User(111, "Siddhant", "Thorat", "sid@gmail.com", "pwd1", "9359479510","Admin");
			when(userRepository.findByFirstName("Siddhant")).thenReturn(Optional.of(user));
			
			when(passwordEncoder.matches("pwd1", user.getPassword())).thenReturn(true);
			
			MvcResult mvcResult = mvc
					.perform(MockMvcRequestBuilders.get(url).header("Authorization",authHeader)).andReturn();

			assertEquals(mvcResult.getResponse().getStatus(), 200);
			String response=mvcResult.getResponse().getContentAsString();
			assertEquals(mapToJson(user),response);
		}
	 	
	 	  @Test
	 	    @DisplayName("Testing Forget Password Controller")
	 	    void testForgetPassword() throws Exception {
	 	        String url = "/forgotPassword";
//	 	        String credentials = Base64.getEncoder().encodeToString("sid@gmail.com:pwd1".getBytes());
//	 	        String authHeader = "Basic " + credentials;
	 	        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("sid@gmail.com","newpassword");
	 	        String body = mapToJson(forgotPasswordDTO);
	 	        setupSecurityContext();
	 	        // Checking when the email is not matched
	 	        when(service.forgetPassword(Mockito.any(ForgotPasswordDTO.class))).thenReturn(true);
	 	        MvcResult mvcResult = mvc
	 	                .perform(MockMvcRequestBuilders.post(url)
	 	                        .contentType(MediaType.APPLICATION_JSON_VALUE)
	 	                        .content(body))
	 	                .andReturn();
	 	       assertEquals(200,mvcResult.getResponse().getStatus() );
	 	        
	 	        String response = mvcResult.getResponse().getContentAsString();
	 	       assertEquals("Password Reset Successfully", response);

	 	        when(service.forgetPassword(Mockito.any(ForgotPasswordDTO.class))).thenReturn(false);
	 	        mvcResult = mvc
	 	                .perform(MockMvcRequestBuilders.post(url)
	 	                        .contentType(MediaType.APPLICATION_JSON_VALUE)
	 	                        .content(body))
	 	                .andReturn();
	 	       assertEquals("Enter correct Email Id", mvcResult.getResponse().getContentAsString());
	 	        assertEquals(200,mvcResult.getResponse().getStatus());
	 	    }
	 	  
	 	 @Test
	 	@DisplayName("Testing Get All Movies Controller")
	 	void testGetAllMovies() throws Exception {
	 		String url = "/all";
	 		setupSecurityContext();
	 		List<Movie> movies = new ArrayList<>();
	 		Movie m1 = new Movie(new Movie.MovieId("Pushpa", "PVR"), 100);
	 		Movie m2 = new Movie(new Movie.MovieId("Antman", "INOX"), 200);
	 		movies.add(m1);
	 		movies.add(m2);
	 		when(movieRepository.findAll()).thenReturn(movies);
	 		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
	 				.andReturn();

	 		String list = mvcResult.getResponse().getContentAsString();
	 		Movie[] ll = mapFromJson(list, Movie[].class);
	 		assertEquals(2, ll.length);

	 		int status = mvcResult.getResponse().getStatus();
	 		assertEquals(200, status);
	 	}
	 	 
	 	@Test
		@DisplayName("Testing Search By Movie Name Controller")
		void testSearchByMovieName() throws Exception {
			String url = "/movies/search/Pus";
			List<Movie> movies = new ArrayList<>();
			movies.add(new Movie(new Movie.MovieId("Pushpa", "PVR"), 100));
			when(service.searchByMovieName(Mockito.anyString())).thenReturn(movies);
			MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();

			String list = mvcResult.getResponse().getContentAsString();
			Movie[] ll = mapFromJson(list, Movie[].class);
			assertEquals(1, ll.length);

			int status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
		}
	 	
	 	@Test
		@DisplayName("Testing Add Movie Controller")
		void testAddMovieController() throws Exception {
			String url = "/movie/add";
			Movie movie = new Movie(new Movie.MovieId("avenger", "pvr"), 250);
			String body = mapToJson(movie);
			when(movieRepository.save(Mockito.any(Movie.class))).thenReturn(movie);
			MvcResult mvcResult = mvc
					.perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
					.andReturn();

			int status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);

			  String response = mvcResult.getResponse().getContentAsString();
			  System.out.println(response); 
			  Movie result = mapFromJson(response,Movie.class); 
			  assertEquals(movie.getId().getMovieName(),
			  result.getId().getMovieName());
		}
		
//		@Test
//		@WithMockUser(username="Siddhant",roles= {"USER"})
//		@DisplayName("Testig Book Ticket Controller")
//		void testBookTicket() throws MovieNotFoundException, Exception{
//			String url = "/bookTickets";
//			List<String> seats = new ArrayList<>();
//			seats.add("J1");
//			seats.add("J2");
//			seats.add("J3");
//			Ticket ticket = new Ticket(678, "Avengers", "PVR", 3, seats);
////			Movie.MovieId key = new Movie.MovieId("Avengers", "PVR");
//			String body = mapToJson(ticket);
//			when(service.updateTicketRemains(Mockito.any(Movie.MovieId.class), Mockito.anyInt())).thenReturn(true);
//			MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(body)).andReturn();
//			int status = mvcResult.getResponse().getStatus();
//			String response = mvcResult.getResponse().getContentAsString();
//			
//			assertEquals(200, status);
//			assertEquals("Ticket Booked Successfully", response);
//		}
		
		@Test
		@DisplayName("Testing Delete Movie Controller")
		void testDeleteMovie() throws Exception{
			String url = "/RRR/delete/PVR";
			MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
			int status = mvcResult.getResponse().getStatus();
			
			String msg = mvcResult.getResponse().getContentAsString();
			
			assertEquals(200, status);
			assertEquals("", msg);
		}
	
}

