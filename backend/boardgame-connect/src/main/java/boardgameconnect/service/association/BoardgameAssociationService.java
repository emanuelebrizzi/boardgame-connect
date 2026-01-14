package boardgameconnect.service.association;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.mapper.AssociationMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import jakarta.transaction.Transactional;

@Service
public class BoardgameAssociationService implements AssociationService {

	private final AssociationRepository associationRepository;
	private final BoardgameRepository boardgameRepository;
	private final AssociationMapper associationMapper;

	public BoardgameAssociationService(AssociationRepository associationRepository,
			BoardgameRepository boardgameRepository, AssociationMapper associationMapper) {
		this.associationRepository = associationRepository;
		this.boardgameRepository = boardgameRepository;
		this.associationMapper = associationMapper;
	}

	@Override
	public List<AssociationSummary> getAssociations() {
		return associationRepository.findAll().stream().map(associationMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<AssociationSummary> getAssociations(String boardgameId) {
		if (!boardgameRepository.existsById(boardgameId)) {
			throw new BoardgameNotFoundException("Boardgame not found with id: " + boardgameId);
		}

		return associationRepository.findByBoardgamesId(boardgameId).stream().map(associationMapper::toDto)
				.collect(Collectors.toList());

	}

	@Override
	@Transactional
	public void addBoardgamesToAssociation(List<String> boardgamesIds, Email associationEmail) {
		Association association = associationRepository.findByAccountEmail(associationEmail).orElseThrow(
				() -> new AssociationNotFoundException("Association not found for email: " + associationEmail));
		List<Boardgame> foundGames = boardgameRepository.findAllById(boardgamesIds);
		association.getBoardgames().addAll(foundGames);
		associationRepository.save(association);
	}

	@Override
	public void deleteBoardgames(List<String> boardgameIds, Email association1Email) {
		// TODO Auto-generated method stub

	}

}
