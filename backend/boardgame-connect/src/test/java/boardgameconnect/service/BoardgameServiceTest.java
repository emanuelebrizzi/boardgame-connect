package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.mapper.BoardgameMapper;
import boardgameconnect.model.Boardgame;

@ExtendWith(MockitoExtension.class)
class BoardgameServiceTest {

	@Mock
	private BoardgameRepository repository;

	@Mock
	private BoardgameMapper mapper;

	@InjectMocks
	private BoardgameService boardgameService;

	private Boardgame game;
	private BoardgameDto dto;

	@BeforeEach
	void setUp() {
		game = new Boardgame("Root", 2, 4, 60, 20, "root.png");
		dto = new BoardgameDto("1", "Root", 2, 4, 60, 20, "root.png");
	}

	@Test
	void getAllBoardgamesShouldReturnListOfDtos() {
		List<Boardgame> games = Arrays.asList(game, new Boardgame());
		when(repository.findAll()).thenReturn(games);
		when(mapper.toDto(any(Boardgame.class))).thenReturn(dto);

		List<BoardgameDto> result = boardgameService.getAllBoardgames();

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(repository, times(1)).findAll();
		verify(mapper, times(2)).toDto(any(Boardgame.class));
	}

	@Test
	void getAllBoardgamesShouldReturnEmptyListWhenNoGamesFound() {
		when(repository.findAll()).thenReturn(List.of());

		List<BoardgameDto> result = boardgameService.getAllBoardgames();

		assertTrue(result.isEmpty());
		verify(repository, times(1)).findAll();
		verifyNoInteractions(mapper);
	}
}