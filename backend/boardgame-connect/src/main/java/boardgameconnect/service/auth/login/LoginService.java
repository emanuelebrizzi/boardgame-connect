package boardgameconnect.service.auth.login;

import boardgameconnect.dto.auth.login.LoginRequest;
import boardgameconnect.dto.auth.login.LoginResponse;

public interface LoginService<T> {
    LoginResponse<T> login(LoginRequest request);
}
