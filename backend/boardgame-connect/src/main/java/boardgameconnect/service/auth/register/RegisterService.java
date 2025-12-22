package boardgameconnect.service.auth.register;

import boardgameconnect.dto.auth.register.RegisterRequest;

public interface RegisterService<T> {
    void register(RegisterRequest<T> request);
}