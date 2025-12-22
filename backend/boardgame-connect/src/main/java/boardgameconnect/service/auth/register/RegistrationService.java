package boardgameconnect.service.auth.register;

import boardgameconnect.dto.auth.register.RegistrationRequest;

public interface RegistrationService<T> {
    void register(RegistrationRequest<T> request);
}