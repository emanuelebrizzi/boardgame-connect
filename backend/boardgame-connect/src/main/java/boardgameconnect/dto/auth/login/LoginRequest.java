package boardgameconnect.dto.auth.login;

import boardgameconnect.model.Email;

public record LoginRequest(Email email, String password) {
}
