package boardgameconnect.model;

public class Player extends User {
	private String username;

	public Player(Email email, String password, String username) {
		super(email, password);
		this.username = username;
	}

	public Object getUsername() {
		return username;
	}

}
