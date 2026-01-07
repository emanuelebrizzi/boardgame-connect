package boardgameconnect.service.reservation;

public interface ParticipationService {

	void join(String reservationId, String playerId);

	void leave(String reservationId, String playerId);

}
