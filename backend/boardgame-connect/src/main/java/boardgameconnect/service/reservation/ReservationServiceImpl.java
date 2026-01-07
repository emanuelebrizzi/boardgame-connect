package boardgameconnect.service.reservation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.ReservationCreateRequest;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.exception.PlayerNotFoundException;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.ReservationStatus;

@Service
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final BoardgameRepository boardgameRepository;
	private final AssociationRepository associationRepository;
	private final PlayerRepository playerRepository;

	public ReservationServiceImpl(ReservationRepository reservationRepository, BoardgameRepository boardgameRepository,
			AssociationRepository associationRepository, PlayerRepository playerRepository) {
		this.reservationRepository = reservationRepository;
		this.boardgameRepository = boardgameRepository;
		this.associationRepository = associationRepository;
		this.playerRepository = playerRepository;

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
				.orElseThrow(() -> new ReservationNotFoundException("Reservation " + id + "not found"));

		AssociationSummary assocSummary = new AssociationSummary(res.getAssociation().getId(),
				res.getAssociation().getAccount().getName(), res.getAssociation().getAddress());

		List<PlayerSummary> playerSummaries = res.getPlayers().stream()
				.map(p -> new PlayerSummary(p.getId(), p.getAccount().getName())).toList();

		return new ReservationDetail(res.getId(), res.getBoardgame().getName(), assocSummary, playerSummaries,
				res.getBoardgame().getMinTime(), res.getBoardgame().getMaxPlayer(), res.getStartTime(),
				res.getEndTime(), res.getStatus().name());
	}

	@Override
	@Transactional
	public void createReservation(ReservationCreateRequest request, Email userEmail) {
		Boardgame game = boardgameRepository.findById(request.boardgameId()).orElseThrow(
				() -> new BoardgameNotFoundException("Boardgame not found with ID: " + request.boardgameId()));

		Association association = associationRepository.findById(request.associationId()).orElseThrow(
				() -> new AssociationNotFoundException("Association not found with ID: " + request.associationId()));

		Player creator = playerRepository.findByEmail(userEmail)
				.orElseThrow(() -> new PlayerNotFoundException("Player profile not found for email: " + userEmail));

		long durationMinutes = game.calculateDuration(request.maxPlayers());
		Instant endTime = request.startTime().plus(Duration.ofMinutes(durationMinutes));

		Reservation reservation = new Reservation(creator, association, game, request.startTime(), endTime);

		if (request.maxPlayers() > game.getMaxPlayer()) {
			throw new IllegalArgumentException("Invalid player numbers " + game.getName());
		}

		reservationRepository.save(reservation);

	}

}