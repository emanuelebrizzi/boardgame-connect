package boardgameconnect.service.reservation;

import boardgameconnect.model.Email;

public interface ParticipationService {

	void join(String reservationId, Email email);

	void leave(String reservationId, Email email);

}
