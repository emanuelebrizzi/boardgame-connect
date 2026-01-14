package boardgameconnect.service.association;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.BoardgameInUseException;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.mapper.AssociationMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.ReservationStatus;
import jakarta.transaction.Transactional;

@Service
public class BoardgameConnectAssociationService implements AssociationService {

	private final AssociationRepository associationRepository;
	private final BoardgameRepository boardgameRepository;
	private final ReservationRepository reservationRepository;
	private final AssociationMapper associationMapper;

	public BoardgameConnectAssociationService(AssociationRepository associationRepository,
			BoardgameRepository boardgameRepository, ReservationRepository reservationRepository,
			AssociationMapper associationMapper) {
		this.associationRepository = associationRepository;
		this.boardgameRepository = boardgameRepository;
		this.reservationRepository = reservationRepository;
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
	@Transactional
	public void removeBoardgamesFromAssociation(List<String> boardgameIds, Email associationEmail) {
		Association association = associationRepository.findByAccountEmail(associationEmail)
				.orElseThrow(() -> new AssociationNotFoundException("Association not found"));

		for (String gameId : boardgameIds) {
			Boardgame gameToRemove = association.getBoardgames().stream().filter(bg -> bg.getId().equals(gameId))
					.findFirst().orElse(null);

			if (gameToRemove == null) {
				continue;
			}

			boolean hasOpenReservations = reservationRepository.existsByAssociationAndBoardgameIdAndStatus(association,
					gameId, ReservationStatus.OPEN);

			if (hasOpenReservations) {
				throw new BoardgameInUseException(
						"Cannot remove " + gameToRemove.getName() + " because it has OPEN reservations.");
			}

			association.getBoardgames().removeIf(bg -> bg.getId().equals(gameId));
		}

		associationRepository.save(association);

	}

}
