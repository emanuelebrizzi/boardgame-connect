package boardgameconnect.model;

public class Player extends User {
	private String username;

	public Player(Email email, String password, String username) {
		super(email, password);
		if ((username == null)) {
			throw new IllegalArgumentException("Username cannnot be null");
		}
		this.username = username;
	}

	public Object getUsername() {
		return username;
	}

}
