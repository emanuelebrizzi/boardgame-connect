package boardgameconnect.service.association;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dao.GameTableRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.dto.GameTableRequest;
import boardgameconnect.dto.GameTableResponse;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.BoardgameInUseException;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.exception.GameTableInUseException;
import boardgameconnect.mapper.AssociationMapper;
import boardgameconnect.mapper.BoardgameMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.GameTable;
import boardgameconnect.model.ReservationStatus;
import jakarta.transaction.Transactional;

@Service
public class AssociationServiceImpl implements AssociationService {

	private final AssociationRepository associationRepository;
	private final BoardgameRepository boardgameRepository;
	private final GameTableRepository gameTableRepository;
	private final ReservationRepository reservationRepository;
	private final AssociationMapper associationMapper;
	private final BoardgameMapper boardgameMapper;

	public AssociationServiceImpl(AssociationRepository associationRepository, BoardgameRepository boardgameRepository,
			GameTableRepository gameTableRepository, ReservationRepository reservationRepository,
			AssociationMapper associationMapper, BoardgameMapper boardgameMapper) {
		this.associationRepository = associationRepository;
		this.boardgameRepository = boardgameRepository;
		this.gameTableRepository = gameTableRepository;
		this.reservationRepository = reservationRepository;
		this.associationMapper = associationMapper;
		this.boardgameMapper = boardgameMapper;
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

	@Override
	public List<BoardgameDto> getBoardgamesFrom(Email associationEmail) {
		Association association = associationRepository.findByAccountEmail(associationEmail)
				.orElseThrow(() -> new AssociationNotFoundException("Association not found"));
		return association.getBoardgames().stream().map(boardgameMapper::toDto).toList();
	}

	@Override
	@Transactional
	public void addTableToAssociation(GameTableRequest request, Email associationEmail) {
		Association association = associationRepository.findByAccountEmail(associationEmail)
				.orElseThrow(() -> new AssociationNotFoundException("Association not found"));

		GameTable table = new GameTable();
		table.setCapacity(request.capacity());
		table.setSize(request.size());
		table.setAssociation(association);
		association.addGameTable(table);

		gameTableRepository.save(table);
	}

	@Override
	@Transactional
	public void removeTableFromAssociation(String tableId, Email associationEmail) {
		Association association = associationRepository.findByAccountEmail(associationEmail)
				.orElseThrow(() -> new AssociationNotFoundException("Association not found"));

		GameTable tableToRemove = association.getGameTables().stream().filter(t -> t.getId().equals(tableId))
				.findFirst().orElseThrow(() -> new NoSuchElementException("Table not found in this association"));

		boolean hasOpenReservations = reservationRepository.existsByGameTableIdAndStatus(tableId,
				ReservationStatus.OPEN);

		if (hasOpenReservations) {
			throw new GameTableInUseException("Cannot remove table because it has OPEN reservations.");
		}

		association.getGameTables().remove(tableToRemove);

		associationRepository.save(association);
	}

	@Override
	@Transactional
	public List<GameTableResponse> getAssociationTablesById(String id) {
		Association association = associationRepository.findById(id)
				.orElseThrow(() -> new AssociationNotFoundException("Association not found with id: " + id));

		return association.getGameTables().stream()
				.map(table -> new GameTableResponse(table.getId(), table.getSize(), table.getCapacity()))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<GameTableResponse> getAssociationTablesByEmail(Email associationEmail) {
		Association association = associationRepository.findByAccountEmail(associationEmail).orElseThrow(
				() -> new AssociationNotFoundException("Association not found for email: " + associationEmail));

		return association.getGameTables().stream()
				.map(table -> new GameTableResponse(table.getId(), table.getSize(), table.getCapacity()))
				.collect(Collectors.toList());
	}

}
