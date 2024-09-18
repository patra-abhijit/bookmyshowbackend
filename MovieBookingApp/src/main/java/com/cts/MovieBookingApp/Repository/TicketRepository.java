package com.cts.MovieBookingApp.Repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cts.MovieBookingApp.Models.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket,Integer>{

	//List<Ticket> findByMovieNameAndtheatherName(String movieName, String theatherName);

}
