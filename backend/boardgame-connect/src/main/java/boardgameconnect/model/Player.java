package boardgameconnect.model;

public class Player extends User {
	private String username;

	public Player(String id, Email email, String password, String username) {
		super(id, email, password);
		if ((username == null)) {
			throw new IllegalArgumentException("Username cannnot be null");
		}
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

}
