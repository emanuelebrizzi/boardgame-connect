package boardgameconnect.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.exception.ReservationNotFoundException;
import boardgameconnect.service.reservation.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    private static final String BASE_URI = "/api/v1/reservations";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

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
}