package boardgameconnect.dto.reservation;

import java.time.Instant;
import java.util.List;

import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.association.AssociationSummary;

public record ReservationDetail(String id, String game, String gameImgPath, AssociationSummary association,
		List<PlayerSummary> players, int minPlayers, int maxPlayers, Instant startTime, Instant endTime, String state) {
}