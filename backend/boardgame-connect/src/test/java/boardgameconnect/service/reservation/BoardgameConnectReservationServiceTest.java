package boardgameconnect.service.reservation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dao.GameTableRepository;
import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.reservation.ReservationCreateRequest;
import boardgameconnect.dto.reservation.ReservationDetail;
import boardgameconnect.dto.reservation.ReservationSummary;
import boardgameconnect.exception.ReservationFullException;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.GameTable;
import boardgameconnect.model.GameTableSize;
import boardgameconnect.model.Player;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class BoardgameConnectReservationServiceTest {

	private static final String ASSOCIATION_EMAIL_STRING = "assoc@example.com";
	private static final String ASSOCIATION_NAME = "BoardGames Inc";
	private static final String ASSOCIATION_ADDRESS = "Via Roma 1";
	private static final String ASSOCIATION_TAXCODE = "TAX123";
	private static final String ASSOCIATION_ID = "assoc_123";

	private static final String PLAYER_EMAIL_STRING = "player@example.com";
	private static final String PLAYER_NAME = "Mario Rossi";

	private static final String BORDGAME_ID = "bg_456";
	private static final String BORDGAME_NAME = "Root";
	private static final String BORDGAME_URL = "url-img-root.jpg";

	private static final String RESERVATION_ID = "r_789";
	private static final String TABLE_ID = "table_999";

	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private BoardgameRepository boardgameRepository;
	@Mock
	private AssociationRepository associationRepository;
	@Mock
	private PlayerRepository playerRepository;
	@Mock
	private GameTableRepository gameTableRepository;

	@InjectMocks
	private BoardgameConnectReservationService reservationService;

	private Reservation openReservation;
	private Boardgame rootGame;
	private Association association;
	private Player creator;

	@BeforeEach
	void setUp() {
		UserAccount assocAcc = new UserAccount(new Email(ASSOCIATION_EMAIL_STRING), "pass", ASSOCIATION_NAME,
				UserRole.ASSOCIATION);
		association = new Association(assocAcc, ASSOCIATION_TAXCODE, ASSOCIATION_ADDRESS);
		ReflectionTestUtils.setField(association, "id", ASSOCIATION_ID);

		rootGame = new Boardgame(BORDGAME_NAME, 2, 4, 60, 30, BORDGAME_URL);
		ReflectionTestUtils.setField(rootGame, "id", BORDGAME_ID);

		UserAccount playerAcc = new UserAccount(new Email(PLAYER_EMAIL_STRING), "pass", PLAYER_NAME, UserRole.PLAYER);
		creator = new Player(playerAcc);

		openReservation = new Reservation(creator, association, rootGame, null, 3, Instant.now(),
				Instant.now().plusSeconds(3600));

		ReflectionTestUtils.setField(openReservation, "id", RESERVATION_ID);
	}

	@Test
	void getAvailableReservationsShouldFilterByDefaultStatusOpen() {
		when(reservationRepository.findAll()).thenReturn(List.of(openReservation));
		List<ReservationSummary> result = reservationService.getAvailableReservations(null, null, null);
		assertEquals(1, result.size());
		verify(reservationRepository, times(1)).findAll();
	}

	@Test
	void getAvailableReservationsShouldFilterByGameName() {
		when(reservationRepository.findAll()).thenReturn(List.of(openReservation));

		List<ReservationSummary> result = reservationService.getAvailableReservations(null, "ROOT", null);

		assertEquals(1, result.size());
		assertEquals("Root", result.get(0).game());
	}

	@Test
	void getAvailableReservationsShouldFilterByAssociationName() {
		when(reservationRepository.findAll()).thenReturn(List.of(openReservation));

		List<ReservationSummary> result = reservationService.getAvailableReservations(null, null, ASSOCIATION_NAME);

		assertEquals(1, result.size());
		assertEquals(ASSOCIATION_NAME, result.get(0).association());
	}

	@Test
	void getAvailableReservationsShouldMapCorrectDTOFields() {
		when(reservationRepository.findAll()).thenReturn(List.of(openReservation));

		ReservationSummary summary = reservationService.getAvailableReservations(null, null, null).get(0);

		assertAll(() -> assertEquals(BORDGAME_NAME, summary.game()),
				() -> assertEquals(ASSOCIATION_NAME, summary.association()),
				() -> assertEquals(1, summary.currentPlayers()), () -> assertEquals(3, summary.maxPlayers()),
				() -> assertNotNull(summary.startTime()), () -> assertNotNull(summary.endTime()));
	}

	@Test
	void getAvailableReservationsShouldReturnEmptyWhenNoMatch() {
		when(reservationRepository.findAll()).thenReturn(List.of(openReservation));

		List<ReservationSummary> result = reservationService.getAvailableReservations(null, "Terra Mystica", null);

		assertTrue(result.isEmpty());
	}

	@Test
	void getReservationByIdShouldReturnDetailWhenExists() {
		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));

		ReservationDetail detail = reservationService.getReservationById(RESERVATION_ID);

		assertNotNull(detail);
		assertAll(() -> assertEquals(RESERVATION_ID, detail.id()), () -> assertEquals(BORDGAME_NAME, detail.game()),
				() -> assertEquals(ASSOCIATION_NAME, detail.association().name()),
				() -> assertEquals(ASSOCIATION_ADDRESS, detail.association().address()),
				() -> assertEquals(1, detail.players().size()),
				() -> assertEquals(PLAYER_NAME, detail.players().get(0).name()),
				() -> assertEquals("OPEN", detail.state()));
		verify(reservationRepository).findById(RESERVATION_ID);
	}

	@Test
	void getReservationByIdShouldThrowExceptionWhenNotFound() {
		String unknownId = "non-existent";
		when(reservationRepository.findById(unknownId)).thenReturn(Optional.empty());

		assertThrows(ReservationNotFoundException.class, () -> {
			reservationService.getReservationById(unknownId);
		});
	}

	@Test
	void createReservationShouldSaveSuccessfullyWhenTableIsAvailable() {
		Instant startTime = Instant.parse("2025-11-25T21:00:00Z");
		ReservationCreateRequest request = new ReservationCreateRequest(BORDGAME_ID, ASSOCIATION_ID, 3, startTime);
		Email userEmail = new Email(PLAYER_EMAIL_STRING);

		GameTable freeTable = new GameTable(TABLE_ID, association, GameTableSize.MEDIUM, 4);

		when(boardgameRepository.findById(BORDGAME_ID)).thenReturn(Optional.of(rootGame));
		when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
		when(playerRepository.findByAccountEmail(userEmail)).thenReturn(Optional.of(creator));

		when(gameTableRepository.findByAssociationAndCapacityGreaterThanEqual(association, 3))
				.thenReturn(List.of(freeTable));
		when(reservationRepository.findOccupiedTables(eq(ASSOCIATION_ID), any(), any())).thenReturn(List.of());

		when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> {
			Reservation toSave = i.getArgument(0);
			ReflectionTestUtils.setField(toSave, "id", "test-uuid-123");
			return toSave;
		});

		ReservationDetail result = reservationService.createReservation(request, userEmail);

		assertNotNull(result);
		assertAll(() -> assertEquals("test-uuid-123", result.id()), () -> assertEquals(BORDGAME_NAME, result.game()),
				() -> assertEquals(3, result.maxPlayers()), () -> assertEquals(startTime, result.startTime()),
				() -> assertEquals("OPEN", result.state()),
				() -> assertEquals(ASSOCIATION_ID, result.association().id()));

		boolean creatorFound = result.players().stream().anyMatch(p -> p.name().equals(PLAYER_NAME));
		assertTrue(creatorFound);

		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}

	@Test
	void createReservationShouldThrowExceptionWhenPlayersAreNotInRange() {
		int invalidPlayers = 10;
		ReservationCreateRequest request = new ReservationCreateRequest(BORDGAME_ID, ASSOCIATION_ID, invalidPlayers,
				Instant.now());
		Email userEmail = new Email(PLAYER_EMAIL_STRING);

		when(boardgameRepository.findById(BORDGAME_ID)).thenReturn(Optional.of(rootGame));
		when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
		when(playerRepository.findByAccountEmail(userEmail)).thenReturn(Optional.of(creator));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			reservationService.createReservation(request, userEmail);
		});
		assertTrue(exception.getMessage().contains("Number of invalid players for " + BORDGAME_NAME));
	}

	@Test
	void createReservationShouldThrowExceptionWhenNoTableIsAvailable() {
		ReservationCreateRequest request = new ReservationCreateRequest(BORDGAME_ID, ASSOCIATION_ID, 4, Instant.now());
		Email userEmail = new Email(PLAYER_EMAIL_STRING);

		GameTable busyTable = new GameTable(TABLE_ID, association, GameTableSize.MEDIUM, 4);

		when(boardgameRepository.findById(BORDGAME_ID)).thenReturn(Optional.of(rootGame));
		when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
		when(playerRepository.findByAccountEmail(userEmail)).thenReturn(Optional.of(creator));

		when(gameTableRepository.findByAssociationAndCapacityGreaterThanEqual(association, 4))
				.thenReturn(List.of(busyTable));
		when(reservationRepository.findOccupiedTables(eq(ASSOCIATION_ID), any(), any())).thenReturn(List.of(busyTable));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			reservationService.createReservation(request, userEmail);
		});
		assertEquals("Table not found.", exception.getMessage());
	}

	@Test
	void createReservationShouldCalculateCorrectEndTime() {
		Instant startTime = Instant.parse("2025-11-25T21:00:00Z");
		int players = 4;
		long expectedDurationMin = rootGame.calculateDuration(players);
		Instant expectedEndTime = startTime.plus(java.time.Duration.ofMinutes(expectedDurationMin));

		ReservationCreateRequest request = new ReservationCreateRequest(BORDGAME_ID, ASSOCIATION_ID, players,
				startTime);
		GameTable freeTable = new GameTable(TABLE_ID, association, GameTableSize.MEDIUM, 4);

		when(boardgameRepository.findById(BORDGAME_ID)).thenReturn(Optional.of(rootGame));
		when(associationRepository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));
		when(playerRepository.findByAccountEmail(any())).thenReturn(Optional.of(creator));
		when(gameTableRepository.findByAssociationAndCapacityGreaterThanEqual(any(), anyInt()))
				.thenReturn(List.of(freeTable));

		when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> {
			Reservation toSave = i.getArgument(0);
			ReflectionTestUtils.setField(toSave, "id", "dummy-id");
			return toSave;
		});

		reservationService.createReservation(request, new Email(PLAYER_EMAIL_STRING));

		org.mockito.ArgumentCaptor<Reservation> reservationCaptor = org.mockito.ArgumentCaptor
				.forClass(Reservation.class);
		verify(reservationRepository).save(reservationCaptor.capture());
		Reservation savedReservation = reservationCaptor.getValue();

		assertEquals(startTime, savedReservation.getStartTime());
		assertEquals(expectedEndTime, savedReservation.getEndTime());
	}

	@Test
	void joinShouldAddPlayerSuccessfully() {
		String otherPlayerEmail = "other@example.com";
		Email email = new Email(otherPlayerEmail);
		UserAccount otherAcc = new UserAccount(email, "pass", "Luigi Verdi", UserRole.PLAYER);
		Player otherPlayer = new Player(otherAcc);

		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));
		when(playerRepository.findByAccountEmail(email)).thenReturn(Optional.of(otherPlayer));

		reservationService.join(RESERVATION_ID, email);

		assertTrue(openReservation.getPlayers().contains(otherPlayer));
		verify(reservationRepository, times(1)).save(openReservation);
	}

	@Test
	void joinShouldThrowExceptionWhenPlayerAlreadyJoined() {
		Email email = new Email(PLAYER_EMAIL_STRING);

		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));
		when(playerRepository.findByAccountEmail(email)).thenReturn(Optional.of(creator));

		assertThrows(boardgameconnect.exception.PlayerAlreadyJoinedException.class, () -> {
			reservationService.join(RESERVATION_ID, email);
		});
		verify(reservationRepository, times(0)).save(any());
	}

	@Test
	void joinShouldThrowExceptionWhenReservationIsFull() {
		Email newPlayerEmail = new Email("new-player@example.com");
		Player newPlayer = new Player(new UserAccount(newPlayerEmail, "pass", "Mario", UserRole.PLAYER));

		for (int i = 0; i < 3; i++) {
			Player p = new Player(new UserAccount(new Email("p" + i + "@test.it"), "pw", "Name", UserRole.PLAYER));
			openReservation.getPlayers().add(p);
		}

		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));
		when(playerRepository.findByAccountEmail(newPlayerEmail)).thenReturn(Optional.of(newPlayer));

		assertThrows(ReservationFullException.class, () -> {
			reservationService.join(RESERVATION_ID, newPlayerEmail);
		});

		verify(reservationRepository, times(0)).save(any());
	}

	@Test
	void joinShouldThrowExceptionWhenReservationNotFound() {
		String unknownId = "non-existent-id";
		Email email = new Email(PLAYER_EMAIL_STRING);

		when(reservationRepository.findById(unknownId)).thenReturn(Optional.empty());

		assertThrows(ReservationNotFoundException.class, () -> {
			reservationService.join(unknownId, email);
		});

		verify(reservationRepository, times(0)).save(any());
	}

	@Test
	void leaveShouldRemovePlayerSuccessfully() {
		Email email = new Email(PLAYER_EMAIL_STRING);
		Player secondPlayer = new Player(new UserAccount(new Email("2@test.com"), "p", "Name", UserRole.PLAYER));
		openReservation.getPlayers().add(secondPlayer);

		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));

		reservationService.leave(RESERVATION_ID, email);

		assertEquals(1, openReservation.getPlayers().size());
		verify(reservationRepository, times(1)).save(openReservation);
	}

	@Test
	void leaveShouldDeleteReservationWhenLastPlayerLeaves() {
		Email email = new Email(PLAYER_EMAIL_STRING);

		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));

		reservationService.leave(RESERVATION_ID, email);

		verify(reservationRepository, times(1)).delete(openReservation);
	}

	@Test
	void leaveShouldThrowExceptionIfPlayerWhenNotInReservation() {
		Email email = new Email("intruder@example.com");

		when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(openReservation));

		assertThrows(boardgameconnect.exception.ForbiddenActionException.class, () -> {
			reservationService.leave(RESERVATION_ID, email);
		});
		verify(reservationRepository, times(0)).save(any());
		verify(reservationRepository, times(0)).delete(any());
	}

	@Test
	void leaveShouldThrowExceptionWhenReservationNotFound() {
		when(reservationRepository.findById("invalid")).thenReturn(Optional.empty());

		assertThrows(ReservationNotFoundException.class, () -> {
			reservationService.leave("invalid", new Email(PLAYER_EMAIL_STRING));
		});
	}

}