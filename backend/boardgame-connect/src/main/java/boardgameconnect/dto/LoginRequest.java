package boardgameconnect.dto;

public class LoginRequest {
	private String email;
	private String password;
	
	public LoginRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}	
	
	public String getPassword() {
		return password;
	}
}
