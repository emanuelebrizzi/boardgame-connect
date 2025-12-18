package boardgameconnect.service;

import boardgameconnect.dto.RegistrationRequest;

public interface RegistrationService<T> {
    void register(RegistrationRequest<T> request);
}