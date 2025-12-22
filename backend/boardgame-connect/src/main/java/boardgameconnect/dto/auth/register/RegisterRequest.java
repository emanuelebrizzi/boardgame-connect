package boardgameconnect.dto.auth.register;

import boardgameconnect.model.Email;

public record RegisterRequest<T>(Email email, String password, String name, T details) {
}