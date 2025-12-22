package boardgameconnect.service;

import boardgameconnect.dto.authorization.LoginRequest;
import boardgameconnect.dto.authorization.LoginResponse;

public interface AuthService<T> {
    LoginResponse<T> login(LoginRequest request);
}
