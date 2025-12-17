package boardgameconnect.dto;

public record LoginResponse<T>(String accessToken, T profile) {
}
