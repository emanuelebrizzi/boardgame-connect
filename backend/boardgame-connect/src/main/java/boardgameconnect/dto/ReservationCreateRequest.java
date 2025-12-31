package boardgameconnect.dto;

import java.time.Instant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(@NotBlank String boardgameId, @NotBlank String associationId,
	@Min(2) int maxPlayers, @NotNull Instant startTime) {
}