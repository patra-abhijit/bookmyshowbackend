package com.cts.MovieBookingApp.dto;

/*DTO = Data transfer object , when you want to expose or accept only specific fields in your api
without directly exposing the entity or model in this case it is user */
public class ForgotPasswordDTO {
	
	public String email;
	public String newPassword;
	
	
	public ForgotPasswordDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public ForgotPasswordDTO(String email, String newPassword) {
		super();
		this.email = email;
		this.newPassword = newPassword;
	}


	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	
}
