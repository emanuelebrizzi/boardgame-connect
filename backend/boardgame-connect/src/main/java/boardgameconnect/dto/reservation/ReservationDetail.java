package boardgameconnect.dto.reservation;

import java.time.Instant;
import java.util.List;

import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.dto.PlayerSummary;

public record ReservationDetail(String id, String game, AssociationSummary association, List<PlayerSummary> players,
		int minPlayers, int maxPlayers, Instant startTime, Instant endTime, String state) {
}