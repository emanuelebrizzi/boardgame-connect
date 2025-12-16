package boardgameconnect.dto;

import boardgameconnect.model.UserAccount;

public record LoginResponse(String accessToken, UserAccount account) {
}
