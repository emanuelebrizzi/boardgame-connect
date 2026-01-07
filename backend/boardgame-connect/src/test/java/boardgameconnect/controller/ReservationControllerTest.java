package boardgameconnect.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.ReservationCreateRequest;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.exception.BusinessLogicException;
import boardgameconnect.exception.ForbiddenActionException;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.model.UserAccount;
import boardgameconnect.service.reservation.ParticipationService;
import boardgameconnect.service.reservation.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

	private static final String BASE_URI = "/api/v1/reservations";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReservationService reservationService;

	@MockitoBean
	private ParticipationService participationService;

	@Autowired
	private ObjectMapper objectMapper;

	private UserAccount mockUser;
	private final String userId = "user-789";

	@BeforeEach
	void setUp() {
		mockUser = new UserAccount();
		ReflectionTestUtils.setField(mockUser, "id", userId);
	}

	@Test
	void getReservationsShouldReturnListOfAvailableReservations() throws Exception {
		var res1 = new ReservationSummary("t_123", "Root", "La Gilda del Cassero", 2, 4,
				Instant.parse("2025-11-20T21:00:00Z"), Instant.parse("2025-11-20T22:30:00Z"));
		var res2 = new ReservationSummary("t_124", "Wingspan", "Ludoteca Svelta", 1, 5,
				Instant.parse("2025-11-21T18:30:00Z"), Instant.parse("2025-11-21T19:30:00Z"));

		when(reservationService.getAvailableReservations(null, null, null)).thenReturn(List.of(res1, res2));

		mockMvc.perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is("t_123")))
				.andExpect(jsonPath("$[0].game", is("Root"))).andExpect(jsonPath("$[1].game", is("Wingspan")));
	}

	@Test
	void getReservationsWithFiltersShouldReturnFilteredList() throws Exception {
		String gameFilter = "Root";
		var res1 = new ReservationSummary("t_123", "Root", "La Gilda del Cassero", 2, 4,
				Instant.parse("2025-11-20T21:00:00Z"), Instant.parse("2025-11-20T22:30:00Z"));

		when(reservationService.getAvailableReservations(null, gameFilter, null)).thenReturn(List.of(res1));

		mockMvc.perform(get(BASE_URI).param("game", gameFilter).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].game", is("Root")));
	}

	@Test
	void getReservationsShouldReturnEmptyListWhenNoReservationsMatch() throws Exception {
		when(reservationService.getAvailableReservations(anyString(), anyString(), anyString())).thenReturn(List.of());

		mockMvc.perform(get(BASE_URI).param("state", "non-existent").param("sortBy", "game")

				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void getReservationByIdShouldReturnDetailWhenFound() throws Exception {
		String reservationId = "t_789";
		var association = new AssociationSummary("a_456", "La Gilda del Cassero", "Via Roma 1");
		var players = List.of(new PlayerSummary("u_123", "Player1"), new PlayerSummary("u_456", "Player2"));

		var detail = new ReservationDetail(reservationId, "Dune: Imperium", association, players, 2, 4,
				Instant.parse("2025-11-25T21:00:00Z"), Instant.parse("2025-11-25T23:00:00Z"), "OPEN");

		when(reservationService.getReservationById(reservationId)).thenReturn(detail);

		mockMvc.perform(get(BASE_URI + "/" + reservationId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(reservationId)))
				.andExpect(jsonPath("$.game", is("Dune: Imperium")))
				.andExpect(jsonPath("$.association.name", is("La Gilda del Cassero")))
				.andExpect(jsonPath("$.association.address", is("Via Roma 1")))
				.andExpect(jsonPath("$.players", hasSize(2))).andExpect(jsonPath("$.players[0].name", is("Player1")))
				.andExpect(jsonPath("$.state", is("OPEN")));
	}

	@Test
	void getReservationByIdShouldReturn404WhenNotFound() throws Exception {
		String unknownId = "non-existent";
		when(reservationService.getReservationById(unknownId))
				.thenThrow(new ReservationNotFoundException("Reservation not found"));

		mockMvc.perform(get(BASE_URI + "/" + unknownId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void getReservationsServerError() throws Exception {
		when(reservationService.getAvailableReservations(null, null, null))
				.thenThrow(new RuntimeException("Database failure"));

		mockMvc.perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andExpect(jsonPath("$.status").value(500))
				.andExpect(jsonPath("$.error").value("Internal Server Error"))
				.andExpect(jsonPath("$.message").value("An internal error occurred"));
	}

	@Test
	void createReservationShouldReturnCreated() throws Exception {
		ReservationCreateRequest validRequest = new ReservationCreateRequest("game-123", "assoc-456", 4,
				Instant.parse("2025-12-31T23:59:00Z"));

		mockMvc.perform(post(BASE_URI).with(csrf())
				.with(authentication(new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList())))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validRequest)))
				.andExpect(status().isCreated());

		verify(reservationService).createReservation(any(ReservationCreateRequest.class), eq(userId));
	}

	@Test
	void createReservationShouldReturn400WhenDataIsInvalid() throws Exception {

		String invalidRequest = "{ }";

		mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(invalidRequest))
				.andExpect(status().isBadRequest());
	}

	@Test
	void joinReservationShouldReturnOk() throws Exception {
		String reservationId = "res-123";

		mockMvc.perform(post(BASE_URI + "/" + reservationId + "/join").with(csrf())
				.with(authentication(new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList())))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(participationService).join(reservationId, userId);
	}

	@Test
	void joinReservationShouldReturnBadRequestWhenAlreadyJoined() throws Exception {
		String reservationId = "res-123";

		doThrow(new BusinessLogicException("Sei già iscritto a questa partita")).when(participationService)
				.join(reservationId, userId);

		mockMvc.perform(post(BASE_URI + "/" + reservationId + "/join").with(csrf())
				.with(authentication(new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Sei già iscritto a questa partita")));
	}

	@Test
	void leaveReservationShouldReturnNoContent() throws Exception {
		String reservationId = "res-123";

		mockMvc.perform(delete(BASE_URI + "/" + reservationId + "/leave").with(csrf())
				.with(authentication(new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()))))
				.andExpect(status().isNoContent());

		verify(participationService).leave(reservationId, userId);
	}

	@Test
	void leaveReservationShouldReturnForbiddenWhenUserNotParticipant() throws Exception {
		String reservationId = "res-123";

		doThrow(new ForbiddenActionException("Non partecipi a questa prenotazione")).when(participationService)
				.leave(anyString(), anyString());

		mockMvc.perform(delete(BASE_URI + "/" + reservationId + "/leave").with(csrf())
				.with(authentication(new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()))))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message", is("Non partecipi a questa prenotazione")));
	}
}