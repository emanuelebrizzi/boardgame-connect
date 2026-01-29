package boardgameconnect.service.reservation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dao.GameTableRepository;
import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.dto.reservation.ReservationCreateRequest;
import boardgameconnect.dto.reservation.ReservationDetail;
import boardgameconnect.dto.reservation.ReservationSummary;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.exception.ForbiddenActionException;
import boardgameconnect.exception.GameTableNotFoundException;
import boardgameconnect.exception.PlayerAlreadyJoinedException;
import boardgameconnect.exception.PlayerNotFoundException;
import boardgameconnect.exception.ReservationFullException;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.GameTable;
import boardgameconnect.model.Player;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.ReservationStatus;

@Service
public class BoardgameConnectReservationService implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final BoardgameRepository boardgameRepository;
	private final AssociationRepository associationRepository;
	private final PlayerRepository playerRepository;
	private final GameTableRepository gameTableRepository;

	public BoardgameConnectReservationService(ReservationRepository reservationRepository,
			BoardgameRepository boardgameRepository, AssociationRepository associationRepository,
			PlayerRepository playerRepository, GameTableRepository gameTableRepository) {
		this.reservationRepository = reservationRepository;
		this.boardgameRepository = boardgameRepository;
		this.associationRepository = associationRepository;
		this.playerRepository = playerRepository;
		this.gameTableRepository = gameTableRepository;

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
		return new ReservationSummary(res.getId(), res.getBoardgame().getName(), res.getBoardgame().getImagePath(),
				res.getAssociation().getAccount().getName(), res.getPlayers().size(), res.getMaxPlayers(),
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

		return new ReservationDetail(res.getId(), res.getBoardgame().getName(), res.getBoardgame().getImagePath(),
				assocSummary, playerSummaries, res.getBoardgame().getMinTimeInMin(), res.getMaxPlayers(),
				res.getStartTime(), res.getEndTime(), res.getStatus().name());
	}

	@Override
	@Transactional
	public ReservationDetail createReservation(ReservationCreateRequest request, Email userEmail) {
		Boardgame game = boardgameRepository.findById(request.boardgameId()).orElseThrow(
				() -> new BoardgameNotFoundException("Boardgame not found with ID: " + request.boardgameId()));

		Association association = associationRepository.findById(request.associationId()).orElseThrow(
				() -> new AssociationNotFoundException("Association not found with ID: " + request.associationId()));

		Player creator = playerRepository.findByAccountEmail(userEmail)
				.orElseThrow(() -> new PlayerNotFoundException("Player profile not found for email: " + userEmail));

		if (request.selectedPlayers() < game.getMinPlayer() || request.selectedPlayers() > game.getMaxPlayer()) {
			throw new IllegalArgumentException("Number of invalid players for " + game.getName());
		}

		long durationMinutes = game.calculateDuration(request.selectedPlayers());
		Instant startTime = request.startTime();
		Instant endTime = startTime.plus(Duration.ofMinutes(durationMinutes));

		GameTable availableTable = findTable(association, startTime, endTime, request.selectedPlayers());

		Reservation reservation = new Reservation(creator, association, game, availableTable, request.selectedPlayers(),
				startTime, endTime);

		return convertToDetail(reservationRepository.save(reservation));

	}

	private ReservationDetail convertToDetail(Reservation res) {
		return new ReservationDetail(res.getId(), res.getBoardgame().getName(), res.getBoardgame().getImagePath(),
				new AssociationSummary(res.getAssociation().getId(), res.getAssociation().getAccount().getName(),
						res.getAssociation().getAddress()),
				res.getPlayers().stream().map(p -> new PlayerSummary(p.getId(), p.getAccount().getName())).toList(),
				res.getBoardgame().getMinPlayer(), res.getMaxPlayers(), res.getStartTime(), res.getEndTime(),
				res.getStatus().name());
	}

	private GameTable findTable(Association association, Instant start, Instant end, int players) {
		List<GameTable> suitableTables = gameTableRepository.findByAssociationAndCapacityGreaterThanEqual(association,
				players);

		List<GameTable> occupiedTables = reservationRepository.findOccupiedTables(association.getId(), start, end);
		return suitableTables.stream().filter(table -> !occupiedTables.contains(table)).findFirst()
				.orElseThrow(() -> new GameTableNotFoundException("Table not found."));
	}

	@Override
	@Transactional
	public void join(String reservationId, Email email) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
		Player player = playerRepository.findByAccountEmail(email)
				.orElseThrow(() -> new PlayerNotFoundException("Player not found"));

		if (reservation.getPlayers().contains(player)) {
			throw new PlayerAlreadyJoinedException("You have already joined this game");
		}
		if (reservation.getPlayers().size() >= reservation.getBoardgame().getMaxPlayer()) {
			throw new ReservationFullException("Reservation is full");
		}

		reservation.getPlayers().add(player);
		reservationRepository.save(reservation);
	}

	@Override
	@Transactional
	public void leave(String reservationId, Email email) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

		boolean removed = reservation.getPlayers().removeIf(p -> p.getAccount().getEmail().equals(email));

		if (!removed) {
			throw new ForbiddenActionException("You are not part of this reservation");
		}

		if (reservation.getPlayers().isEmpty()) {
			reservationRepository.delete(reservation);
		} else {
			reservationRepository.save(reservation);
		}
	}

}