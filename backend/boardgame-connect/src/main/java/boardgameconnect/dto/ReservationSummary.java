package boardgameconnect.dto;

import java.time.Instant;

public record ReservationSummary(String id, String game, String association, int currentPlayers, int maxPlayers,
	Instant startTime, Instant endTime) {
}
