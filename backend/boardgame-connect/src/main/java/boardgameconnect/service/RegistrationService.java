package boardgameconnect.service;

import boardgameconnect.dto.RegistrationRequest;

public interface RegistrationService<T, D> {
    T register(RegistrationRequest<D> request);
}