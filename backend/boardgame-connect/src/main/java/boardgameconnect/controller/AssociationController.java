package boardgameconnect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.service.association.AssociationService;

@RestController
@RequestMapping("/api/v1/associations")
public class AssociationController {

	private final AssociationService associationService;

	public AssociationController(AssociationService associationService) {
		this.associationService = associationService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<List<AssociationSummary>> getAssociations() {
		List<AssociationSummary> associations = associationService.getAllAssociations();
		return ResponseEntity.ok(associations);
	}
}
