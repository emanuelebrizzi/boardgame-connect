package boardgameconnect.service;

import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
