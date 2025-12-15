package boardgameconnect.dto;

public class UserResponse {
    private String id;
    private String email;
    private String username;
    
    public UserResponse(String id, String email, String username) {
		super();
        this.id = id;
        this.email = email;
        this.username = username;
    }

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}
    
}
