package boardgameconnect.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import boardgameconnect.config.SecurityConfig;
import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.dto.reservation.ReservationCreateRequest;
import boardgameconnect.dto.reservation.ReservationDetail;
import boardgameconnect.dto.reservation.ReservationSummary;
import boardgameconnect.exception.PlayerAlreadyJoinedException;
import boardgameconnect.model.Email;
import boardgameconnect.service.reservation.ReservationService;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
class ReservationControllerTest {

	private static final String RESERVATION_ID = "res-123";
	private static final String BASE_URI = "/api/v1/reservations";
	private static final Email TEST_USER_EMAIL = new Email("test-user@domain.com");

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReservationService reservationService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@Test
	void getReservationsShouldReturnListOfAvailableReservationsByDefault() throws Exception {
		var res1 = new ReservationSummary("t_123", "Root", "root.png", "La Gilda del Cassero", 2, 4,
				Instant.parse("2025-11-20T21:00:00Z"), Instant.parse("2025-11-20T22:30:00Z"), "OPEN", null);
		var res2 = new ReservationSummary("t_124", "Wingspan", "wingspan.png", "Ludoteca Svelta", 1, 5,
				Instant.parse("2025-11-21T18:30:00Z"), Instant.parse("2025-11-21T19:30:00Z"), "OPEN", null);

		when(reservationService.getAvailableReservations(null, null, null)).thenReturn(List.of(res1, res2));

		mockMvc.perform(get(BASE_URI)
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is("t_123")))
				.andExpect(jsonPath("$[1].id", is("t_124")));

		verify(reservationService).getAvailableReservations(null, null, null);
	}

	@Test
	void getReservationsShouldReturnBadRequestWhenStateIsInvalid() throws Exception {
		String invalidState = "NON_EXISTING_STATUS";

		mockMvc.perform(get(BASE_URI).param("state", invalidState)
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", containsString("Validation failed")));

		verifyNoInteractions(reservationService);
	}

	@Test
	void createReservationShouldReturnCreated() throws Exception {
		ReservationCreateRequest validRequest = new ReservationCreateRequest("game-123", "assoc-456", 4,
				Instant.parse("2025-12-31T23:59:00Z"));

		mockMvc.perform(post(BASE_URI)
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validRequest)))
				.andExpect(status().isCreated());

		verify(reservationService).createReservation(eq(validRequest), eq(TEST_USER_EMAIL));
	}

	@Test
	void getReservationByIdShouldReturnDetailWhenFound() throws Exception {
		String reservationId = "res-001";
		var association = new AssociationSummary("a-123", "Ludoteca", "Via Roma");
		var players = List.of(new PlayerSummary("p-123", "Alice"), new PlayerSummary("p-456", "Bob"));
		var detail = new ReservationDetail(reservationId, "Catan", "catan.png", association, players, 3, 4,
				Instant.now(), Instant.now().plusSeconds(7200), "OPEN");

		when(reservationService.getReservationById(reservationId)).thenReturn(detail);

		mockMvc.perform(get(BASE_URI + "/" + reservationId)
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(reservationId))).andExpect(jsonPath("$.players", hasSize(2)))
				.andExpect(jsonPath("$.players[0].name", is("Alice")));

		verify(reservationService).getReservationById(reservationId);
	}

	@Test
	void joinReservationShouldReturnOk() throws Exception {
		String reservationId = RESERVATION_ID;

		mockMvc.perform(post(BASE_URI + "/" + reservationId + "/join")
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(reservationService).join(reservationId, TEST_USER_EMAIL);
	}

	@Test
	void leaveReservationShouldRespectInteractionOrder() throws Exception {
		String reservationId = RESERVATION_ID;

		mockMvc.perform(delete(BASE_URI + "/" + reservationId + "/leave")
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

		verify(reservationService).leave(reservationId, TEST_USER_EMAIL);
	}

	@Test
	void createReservationShouldFailIfDataIsInvalid() throws Exception {
		String invalidRequest = "{}";

		mockMvc.perform(post(BASE_URI)
				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content(invalidRequest)).andExpect(status().isBadRequest());

		verifyNoInteractions(reservationService);
	}

	@Test
	void joinReservationShouldReturnBadRequestIfAlreadyJoined() throws Exception {
		String reservationId = "res-456";

		doThrow(new PlayerAlreadyJoinedException("Already joined reservation")).when(reservationService)
				.join(reservationId, TEST_USER_EMAIL);

		mockMvc.perform(post(BASE_URI + "/" + reservationId + "/join")

				.with(jwt().jwt(j -> j.claim("sub", TEST_USER_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Already joined reservation")));

		verify(reservationService).join(reservationId, TEST_USER_EMAIL);
	}
}