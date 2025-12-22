package boardgameconnect.dto.auth.login;

public record LoginResponse<T>(String accessToken, T profile) {
}
