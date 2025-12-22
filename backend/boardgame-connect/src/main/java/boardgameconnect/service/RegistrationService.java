package boardgameconnect.service;

import boardgameconnect.dto.authorization.RegistrationRequest;

public interface RegistrationService<T> {
    void register(RegistrationRequest<T> request);
}