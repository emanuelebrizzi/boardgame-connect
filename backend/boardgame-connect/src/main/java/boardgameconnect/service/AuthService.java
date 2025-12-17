package boardgameconnect.service;

import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;

public interface AuthService<T> {
    LoginResponse<T> login(LoginRequest request);
}
