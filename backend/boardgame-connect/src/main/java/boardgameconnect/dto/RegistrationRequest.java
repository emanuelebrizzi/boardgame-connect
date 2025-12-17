package boardgameconnect.dto;

import boardgameconnect.model.Email;

public record RegistrationRequest<T>(Email email, String password, String username, T details) {
}