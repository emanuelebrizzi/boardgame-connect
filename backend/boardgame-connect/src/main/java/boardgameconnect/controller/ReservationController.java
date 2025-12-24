package boardgameconnect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.service.reservation.ReservationService;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
	this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationSummary>> getReservations(@RequestParam(required = false) String state,
	    @RequestParam(required = false) String game, @RequestParam(required = false) String association) {

	List<ReservationSummary> reservations = reservationService.getAvailableReservations(state, game, association);

	return ResponseEntity.ok(reservations);
    }
}