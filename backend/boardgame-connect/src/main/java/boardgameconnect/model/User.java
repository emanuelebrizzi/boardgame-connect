package boardgameconnect.model;

public class User {
	private Email email;
	private String password;
	
	public User(Email email, String password) {
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
