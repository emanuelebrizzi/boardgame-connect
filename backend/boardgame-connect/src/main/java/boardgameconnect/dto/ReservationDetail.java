package boardgameconnect.dto;

import java.time.Instant;
import java.util.List;

public record ReservationDetail(String id, String game, AssociationSummary association, List<PlayerSummary> players,
		int minPlayers, int maxPlayers, Instant startTime, Instant endTime, String state) {
}