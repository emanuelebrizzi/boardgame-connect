package boardgameconnect.dto;

import boardgameconnect.model.Email;

public class LoginRequest {
    private Email email;
    private String password;

    public LoginRequest(Email email, String password) {
	super();
	this.email = email;
	this.password = password;
    }

    public Email getEmail() {
	return email;
    }

    public String getPassword() {
	return password;
    }
}
