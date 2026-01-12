package boardgameconnect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.service.BoardgameService;

@RestController
@RequestMapping("/boardgames")
public class BoardgameController {

	private final BoardgameService boardgameService;

	public BoardgameController(BoardgameService boardgameService) {
		this.boardgameService = boardgameService;
	}

	@GetMapping
	public ResponseEntity<List<BoardgameDto>> getBoardgames() {
		List<BoardgameDto> games = boardgameService.getAllBoardgames();
		return ResponseEntity.ok(games);
	}
}