package boardgameconnect.model;

public class User {
	private String id;
	private Email email;
	private String password;

	public User(String id, Email email, String password) {
		super();
		this.id = id ;
		this.email = email;
		this.password = password;
		if (password.isBlank()) {
			throw new IllegalArgumentException("Password cannot be blank");
		}
	}

	public String getId() {
		return id;
	}

	
	public Email getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
	
	

}
