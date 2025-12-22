package boardgameconnect.dto.authorization;

import boardgameconnect.model.Email;

public record LoginRequest(Email email, String password) {
}
