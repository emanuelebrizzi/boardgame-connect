package boardgameconnect.service;

import org.springframework.stereotype.Service;

import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.dto.UserResponse;

@Service
public class AuthenticationService {

	public LoginResponse login(LoginRequest request) {

		// TODO: sostituire con lookup su database + password encoder
		String email = "mario.rossi@example.com";
		String password = "password";

		if (!email.equalsIgnoreCase(request.getEmail()) || !password.equals(request.getPassword())) {
			throw new RuntimeException("Credenziali non valide");
		}

	return null;
	}
}
