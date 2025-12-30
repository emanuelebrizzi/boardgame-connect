package boardgameconnect.service.reservation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    private static final String ASSOCIATION_EMAIL_STRING = "assoc@example.com";
    private static final String ASSOCIATION_NAME = "BoardGames Inc";
    private static final String ASSOCIATION_ADDRESS = "Via Roma 1";
    private static final String ASSOCIATION_TAXCODE = "TAX123";

    private static final String PLAYER_EMAIL_STRING = "player@example.com";
    private static final String PLAYER_NAME = "Mario Rossi";

    private static final String BORDGAME_NAME = "Root";
    private static final String RESERVATION_ID = "t_789";

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation openReservation;

    @BeforeEach
    void setUp() {
	UserAccount assocAcc = new UserAccount(new Email(ASSOCIATION_EMAIL_STRING), "pass", ASSOCIATION_NAME,
		UserRole.ASSOCIATION);
	Association association = new Association(assocAcc, ASSOCIATION_TAXCODE, ASSOCIATION_ADDRESS);

	Boardgame root = new Boardgame(BORDGAME_NAME, 2, 4, 60, 30);

	UserAccount playerAcc = new UserAccount(new Email(PLAYER_EMAIL_STRING), "pass", PLAYER_NAME, UserRole.PLAYER);
	Player creator = new Player(playerAcc);

	openReservation = new Reservation(creator, association, root, Instant.now(), Instant.now().plusSeconds(3600));
	new Reservation(creator, association, root, Instant.now(), Instant.now().plusSeconds(3600));

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
		() -> assertEquals(1, summary.currentPlayers()), () -> assertEquals(4, summary.maxPlayers()),
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

}