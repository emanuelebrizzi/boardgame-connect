package boardgameconnect.controller;

import java.util.List;

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

import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.dto.GameTableRequest;
import boardgameconnect.dto.GameTableResponse;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.model.Email;
import boardgameconnect.service.association.AssociationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/associations")
public class AssociationController {

	private final AssociationService associationService;

	public AssociationController(AssociationService associationService) {
		this.associationService = associationService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<List<AssociationSummary>> getAllAssociations() {
		return ResponseEntity.ok(associationService.getAssociations());
	}

	@GetMapping(params = "boardgameId")
	@PreAuthorize("hasAnyRole('PLAYER', 'ASSOCIATION')")
	public ResponseEntity<List<AssociationSummary>> getAssociationsByGame(@RequestParam String boardgameId) {
		return ResponseEntity.ok(associationService.getAssociations(boardgameId));
	}

	@GetMapping("/boardgames")
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<List<BoardgameDto>> getAssociationBoardgames() {
		var associationEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		List<BoardgameDto> boardgames = associationService.getBoardgamesFrom(associationEmail);
		return ResponseEntity.ok(boardgames);
	}

	@GetMapping("/{id}/boardgames")
	public ResponseEntity<List<BoardgameDto>> getAssociationBoardgames(@PathVariable String id) {
		List<BoardgameDto> boardgames = associationService.getAssociationBoardgames(id);
		return ResponseEntity.ok(boardgames);
	}

	@PostMapping("/boardgames")
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<Void> addAssociationGames(@Valid @RequestBody List<String> boardgamesIds) {
		var associationEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		associationService.addBoardgamesToAssociation(boardgamesIds, associationEmail);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/boardgames")
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<Void> removeAssociationGames(@RequestBody List<String> boardgamesIds) {
		var associationEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		associationService.removeBoardgamesFromAssociation(boardgamesIds, associationEmail);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/tables")
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<Void> addTable(@Valid @RequestBody GameTableRequest tableRequest) {
		var associationEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		associationService.addTableToAssociation(tableRequest, associationEmail);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/tables/{tableId}")
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<Void> removeTable(@PathVariable String tableId) {
		var associationEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		associationService.removeTableFromAssociation(tableId, associationEmail);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/tables")
	@PreAuthorize("hasRole('ASSOCIATION')")
	public ResponseEntity<List<GameTableResponse>> getTables() {
		var associationEmail = new Email(SecurityContextHolder.getContext().getAuthentication().getName());
		return ResponseEntity.ok(associationService.getAssociationTablesByEmail(associationEmail));
	}

	@GetMapping("/{id}/tables")
	public ResponseEntity<List<GameTableResponse>> getTables(@PathVariable String id) {
		return ResponseEntity.ok(associationService.getAssociationTablesById(id));
	}

}
