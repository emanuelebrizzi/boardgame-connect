package boardgameconnect.dto.reservation;

import jakarta.validation.constraints.Pattern;

public record ReservationFilterRequest(

		@Pattern(regexp = "OPEN|CLOSED|open|closed", message = "State must be either OPEN or CLOSED") String state,

		String game,

		String association) {
}
