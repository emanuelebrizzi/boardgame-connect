package boardgameconnect.service;

import java.util.List;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.mapper.BoardgameMapper;

@Service
public class BoardgameService {
	private final BoardgameRepository repository;
	private final BoardgameMapper mapper;

	public BoardgameService(BoardgameRepository repository, BoardgameMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	public List<BoardgameDto> getAllBoardgames() {
		return repository.findAll().stream().map(mapper::toDto).toList();
	}
}