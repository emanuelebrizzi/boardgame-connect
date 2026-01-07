package boardgameconnect.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.exception.BusinessLogicException;
import boardgameconnect.exception.ForbiddenActionException;
import boardgameconnect.exception.PlayerNotFoundException;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.Reservation;

@Service
public class ParticipationServiceImpl implements ParticipationService {

	private final ReservationRepository reservationRepository;
	private final PlayerRepository playerRepository;

	public ParticipationServiceImpl(ReservationRepository reservationRepository, PlayerRepository playerRepository) {
		this.reservationRepository = reservationRepository;
		this.playerRepository = playerRepository;
	}

	@Override
	@Transactional
	public void join(String reservationId, Email email) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ReservationNotFoundException("Prenotazione non trovata"));
		Player player = playerRepository.findByEmail(email)
				.orElseThrow(() -> new PlayerNotFoundException("Giocatore non trovato"));

		if (reservation.getPlayers().contains(player)) {
			throw new BusinessLogicException("Sei già iscritto a questa partita");
		}

		reservation.getPlayers().add(player);
		reservationRepository.save(reservation);
	}

	@Override
	@Transactional
	public void leave(String reservationId, Email email) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ReservationNotFoundException("Prenotazione non trovata"));

		boolean removed = reservation.getPlayers().removeIf(p -> p.getAccount().getEmail().equals(email));

		if (!removed) {
			throw new ForbiddenActionException("Non partecipi a questa prenotazione");
		}

		if (reservation.getPlayers().isEmpty()) {
			reservationRepository.delete(reservation);
		} else {
			reservationRepository.save(reservation);
		}
	}
}