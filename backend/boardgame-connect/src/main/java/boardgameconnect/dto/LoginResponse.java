package boardgameconnect.dto;

import boardgameconnect.model.User;

public class LoginResponse {
    private String accessToken;
    private User user;

    public LoginResponse(String accessToken, User user) {
	this.accessToken = accessToken;
	this.user = user;
    }

    public String getAccessToken() {
	return accessToken;
    }

    public User getUser() {
	return user;
    }
}
