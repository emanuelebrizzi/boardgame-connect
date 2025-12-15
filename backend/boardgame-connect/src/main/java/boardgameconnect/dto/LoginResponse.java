package boardgameconnect.dto;

public class LoginResponse {
    private String accessToken;
    private UserResponse user;
    
    public LoginResponse(String accessToken, UserResponse user) {
		super();
        this.accessToken = accessToken;
        this.user = user;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public UserResponse getUser() {
        return user;
    }
}
