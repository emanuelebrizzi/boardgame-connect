package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.UserRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.model.User;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	this.userRepository = userRepository;
	this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
	User user = userRepository.findByEmail(request.getEmail())
		.orElseThrow(() -> new RuntimeException("Invalid credentials"));

	if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	    throw new RuntimeException("Invalid credentials");
	}

	String tokenPlaceholder = "valid token";
	return new LoginResponse(tokenPlaceholder, user);
    }
}
