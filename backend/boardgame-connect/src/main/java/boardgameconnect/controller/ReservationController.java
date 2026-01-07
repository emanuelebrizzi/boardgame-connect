package boardgameconnect.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import boardgameconnect.model.Email;
import boardgameconnect.service.reservation.ParticipationService;
import boardgameconnect.service.reservation.ReservationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

	private final ReservationService reservationService;
	private final ParticipationService participationService;

	public ReservationController(ReservationService reservationService, ParticipationService participationService) {
		this.reservationService = reservationService;
		this.participationService = participationService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<List<ReservationSummary>> getReservations(@RequestParam(required = false) String state,
			@RequestParam(required = false) String game, @RequestParam(required = false) String association) {
		List<ReservationSummary> reservations = reservationService.getAvailableReservations(state, game, association);
		return ResponseEntity.ok(reservations);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<ReservationDetail> getReservationById(@PathVariable String id) {
		ReservationDetail detail = reservationService.getReservationById(id);
		return ResponseEntity.ok(detail);
	}

	@PostMapping
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<Void> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		var email = new Email(userEmail);
		reservationService.createReservation(request, email);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/{id}/join")
	public ResponseEntity<Void> join(@PathVariable String id) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		var email = new Email(userEmail);
		participationService.join(id, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/leave")
	public ResponseEntity<Void> leave(@PathVariable String id) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		var email = new Email(userEmail);
		participationService.leave(id, email);
		return ResponseEntity.noContent().build();
	}

}