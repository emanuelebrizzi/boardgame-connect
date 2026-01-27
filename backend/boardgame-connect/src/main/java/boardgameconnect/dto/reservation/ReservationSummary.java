package boardgameconnect.dto.reservation;

import java.time.Instant;

public record ReservationSummary(String id, String game, String gameImgPath, String association, int currentPlayers,
		int maxPlayers, Instant startTime, Instant endTime) {
}
