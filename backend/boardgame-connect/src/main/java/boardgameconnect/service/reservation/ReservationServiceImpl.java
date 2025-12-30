package boardgameconnect.service.reservation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.ReservationStatus;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
	this.reservationRepository = reservationRepository;
    }

    @Override
    public List<ReservationSummary> getAvailableReservations(String state, String game, String association) {

	return reservationRepository.findAll().stream()
		.filter(res -> state == null ? res.getStatus() == ReservationStatus.OPEN
			: res.getStatus().name().equalsIgnoreCase(state))
		.filter(res -> game == null || res.getBoardgame().getName().equalsIgnoreCase(game))
		.filter(res -> association == null
			|| res.getAssociation().getAccount().getName().equalsIgnoreCase(association))
		.map(this::mapToSummary).collect(Collectors.toList());
    }

    private ReservationSummary mapToSummary(Reservation res) {
	return new ReservationSummary(res.getId(), res.getBoardgame().getName(),
		res.getAssociation().getAccount().getName(), res.getPlayers().size(), res.getBoardgame().getMaxPlayer(),
		res.getStartTime(), res.getEndTime());
    }

    @Override
    public ReservationDetail getReservationById(String id) {
	Reservation res = reservationRepository.findById(id)
		.orElseThrow(() -> new ReservationNotFoundException("Prenotazione " + id + " non trovata"));

	AssociationSummary assocSummary = new AssociationSummary(res.getAssociation().getId(),
		res.getAssociation().getAccount().getName(), res.getAssociation().getAddress());

	List<PlayerSummary> playerSummaries = res.getPlayers().stream()
		.map(p -> new PlayerSummary(p.getId(), p.getAccount().getName())).toList();

	return new ReservationDetail(res.getId(), res.getBoardgame().getName(), assocSummary, playerSummaries,
		res.getBoardgame().getMinTime(), res.getBoardgame().getMaxPlayer(), res.getStartTime(),
		res.getEndTime(), res.getStatus().name());
    }

}