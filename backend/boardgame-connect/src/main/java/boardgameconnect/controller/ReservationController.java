package boardgameconnect.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.ReservationCreateRequest;
import boardgameconnect.dto.ReservationDetail;
import boardgameconnect.dto.ReservationSummary;
import boardgameconnect.model.UserAccount;
import boardgameconnect.service.reservation.ReservationService;
import jakarta.validation.Valid;

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

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDetail> getReservationById(@PathVariable String id) {
	ReservationDetail detail = reservationService.getReservationById(id);
	return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<Void> createReservation(@Valid @RequestBody ReservationCreateRequest request,
	    @AuthenticationPrincipal UserAccount currentUser) {

	reservationService.createReservation(request, currentUser.getId());

	return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}