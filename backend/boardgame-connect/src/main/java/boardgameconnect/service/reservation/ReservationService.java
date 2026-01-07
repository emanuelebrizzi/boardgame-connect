package boardgameconnect.service.reservation;

import java.util.List;

import boardgameconnect.dto.ReservationCreateRequest;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;

public interface ReservationService {

	public List<ReservationSummary> getAvailableReservations(String state, String game, String association);

	public ReservationDetail getReservationById(String id);

	public void createReservation(ReservationCreateRequest request, String currentUserId);
}
