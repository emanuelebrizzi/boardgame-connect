package boardgameconnect.dto.authorization;

public record LoginResponse<T>(String accessToken, T profile) {
}
