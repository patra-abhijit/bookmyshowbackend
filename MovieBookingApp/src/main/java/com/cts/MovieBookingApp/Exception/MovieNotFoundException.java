package com.cts.MovieBookingApp.Exception;

public class MovieNotFoundException extends Exception {
	
private static final long serialVersionUID = 1L;
	
	public MovieNotFoundException(String str) {
		super(str);
	}
}
