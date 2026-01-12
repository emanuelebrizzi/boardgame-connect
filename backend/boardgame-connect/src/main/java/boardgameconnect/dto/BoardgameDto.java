package boardgameconnect.dto;

public record BoardgameDto(String id, String name, int minPlayers, int maxPlayers, int timeMin, int timePerPlayer,
		String imagePath) {
}