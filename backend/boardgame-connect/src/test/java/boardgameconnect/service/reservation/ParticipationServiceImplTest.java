package boardgameconnect.service.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.exception.BusinessLogicException;
import boardgameconnect.exception.ForbiddenActionException;
import boardgameconnect.exception.PlayerNotFoundException;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Player;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.UserAccount;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceImplTest {

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private PlayerRepository playerRepository;

	@InjectMocks
	private ParticipationServiceImpl participationService;

	private Player mockPlayer;
	private Reservation mockReservation;
	private final String userId = "user-789";
	private final String reservationId = "res-123";

	@BeforeEach
	void setUp() {
		UserAccount account = new UserAccount();
		mockPlayer = new Player(account);
		ReflectionTestUtils.setField(mockPlayer, "id", userId);

		mockReservation = new Reservation();
		ReflectionTestUtils.setField(mockReservation, "id", reservationId);

		List<Player> players = new ArrayList<>();
		ReflectionTestUtils.setField(mockReservation, "players", players);
	}

	@Test
	void joinShouldAddPlayerWhenValidRequest() {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));
		when(playerRepository.findById(userId)).thenReturn(Optional.of(mockPlayer));

		participationService.join(reservationId, userId);

		assertTrue(mockReservation.getPlayers().contains(mockPlayer));
		verify(reservationRepository, times(1)).save(mockReservation);
	}

	@Test
	void joinShouldThrowExceptionWhenPlayerAlreadyJoined() {
		mockReservation.getPlayers().add(mockPlayer);

		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));
		when(playerRepository.findById(userId)).thenReturn(Optional.of(mockPlayer));

		BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
			participationService.join(reservationId, userId);
		});

		assertEquals("Sei già iscritto a questa partita", exception.getMessage());
		verify(reservationRepository, never()).save(any());
	}

	@Test
	void joinShouldThrowExceptionWhenReservationNotFound() {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

		assertThrows(ReservationNotFoundException.class, () -> {
			participationService.join(reservationId, userId);
		});
	}

	@Test
	void joinShouldThrowExceptionWhenPlayerNotFound() {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));
		when(playerRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(PlayerNotFoundException.class, () -> {
			participationService.join(reservationId, userId);
		});
	}

	@Test
	void leaveShouldDeleteReservationWhenLastPlayerLeaves() {
		mockReservation.getPlayers().add(mockPlayer);
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));

		participationService.leave(reservationId, userId);

		assertTrue(mockReservation.getPlayers().isEmpty());
		verify(reservationRepository, times(1)).delete(mockReservation);
		verify(reservationRepository, never()).save(any());
	}

	@Test
	void leaveShouldSaveReservationWhenOtherPlayersRemain() {
		Player anotherPlayer = new Player(new UserAccount());
		ReflectionTestUtils.setField(anotherPlayer, "id", "other-user");

		mockReservation.getPlayers().add(mockPlayer);
		mockReservation.getPlayers().add(anotherPlayer);

		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));

		participationService.leave(reservationId, userId);

		assertEquals(1, mockReservation.getPlayers().size());
		verify(reservationRepository, times(1)).save(mockReservation);
		verify(reservationRepository, never()).delete(any());
	}

	@Test
	void leaveShouldThrowForbiddenWhenUserIsNotParticipant() {
		Player anotherPlayer = new Player(new UserAccount());
		ReflectionTestUtils.setField(anotherPlayer, "id", "other-user");
		mockReservation.getPlayers().add(anotherPlayer);

		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));

		ForbiddenActionException exception = assertThrows(ForbiddenActionException.class, () -> {
			participationService.leave(reservationId, userId);
		});

		assertEquals("Non partecipi a questa prenotazione", exception.getMessage());
		verify(reservationRepository, never()).delete(any());
		verify(reservationRepository, never()).save(any());
	}

	@Test
	void leaveShouldThrowNotFoundWhenReservationDoesNotExist() {
		when(reservationRepository.findById(anyString())).thenReturn(Optional.empty());

		assertThrows(ReservationNotFoundException.class, () -> {
			participationService.leave(reservationId, userId);
		});

		verify(reservationRepository, never()).delete(any());
		verify(reservationRepository, never()).save(any());
	}

}