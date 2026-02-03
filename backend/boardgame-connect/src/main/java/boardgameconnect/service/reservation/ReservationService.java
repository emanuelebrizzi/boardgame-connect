package boardgameconnect.service.reservation;

import java.util.List;

import boardgameconnect.dto.reservation.ReservationCreateRequest;
import boardgameconnect.dto.reservation.ReservationDetail;
import boardgameconnect.dto.reservation.ReservationSummary;
import boardgameconnect.model.Email;

public interface ReservationService {

	List<ReservationSummary> getAvailableReservations(String state, String game, String association);

	ReservationDetail getReservationById(String id);

	String createReservation(ReservationCreateRequest request, Email userEmail);

	void join(String reservationId, Email email);

	void leave(String reservationId, Email email);
}
