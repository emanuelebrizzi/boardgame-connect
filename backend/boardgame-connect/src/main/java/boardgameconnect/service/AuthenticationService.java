package boardgameconnect.service;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.UserRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.model.User;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
	this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
	User user = userRepository.findByEmail(request.getEmail())
		.orElseThrow(() -> new RuntimeException("Invalid credentials"));

	if (!user.getPassword().equals(request.getPassword())) {
	    throw new RuntimeException("Invalid credentials");
	}

	String tokenPlaceholder = "valid token";
	return new LoginResponse(tokenPlaceholder, user);
    }
}
