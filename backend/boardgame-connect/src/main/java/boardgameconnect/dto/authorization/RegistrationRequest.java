package boardgameconnect.dto.authorization;

import boardgameconnect.model.Email;

public record RegistrationRequest<T>(Email email, String password, String name, T details) {
}