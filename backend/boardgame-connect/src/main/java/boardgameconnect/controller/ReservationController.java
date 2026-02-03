package boardgameconnect.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.reservation.ReservationCreateRequest;
import boardgameconnect.dto.reservation.ReservationDetail;
import boardgameconnect.dto.reservation.ReservationFilterRequest;
import boardgameconnect.dto.reservation.ReservationSummary;
import boardgameconnect.model.Email;
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
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<List<ReservationSummary>> getReservations(
			@Valid @ModelAttribute ReservationFilterRequest filter) {
		List<ReservationSummary> reservations = reservationService.getAllReservations(filter.state(), filter.game(),
				filter.association());

		return ResponseEntity.ok(reservations);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<ReservationDetail> getReservationById(@PathVariable String id) {
		ReservationDetail detail = reservationService.getReservationById(id);
		return ResponseEntity.ok(detail);
	}

	@PostMapping
	@PreAuthorize("hasRole('PLAYER')")
	public ResponseEntity<Map<String, String>> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
		var playerEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		String reservationId = reservationService.createReservation(request, playerEmail);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", reservationId));
	}

	@PostMapping("/{id}/join")
	@PreAuthorize("hasRole('PLAYER')")
	public ResponseEntity<Void> join(@PathVariable String id) {
		var playerEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		reservationService.join(id, playerEmail);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/leave")
	@PreAuthorize("hasRole('PLAYER')")
	public ResponseEntity<Void> leave(@PathVariable String id) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		var email = new Email(userEmail);
		reservationService.leave(id, email);
		return ResponseEntity.noContent().build();
	}

}