package boardgameconnect.service.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dao.GameTableRepository;
import boardgameconnect.dao.ReservationRepository;
import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.dto.GameTableRequest;
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
import boardgameconnect.model.GameTableSize;
import boardgameconnect.model.ReservationStatus;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class AssociationServiceImplTest {

	private static final String ASSOCIATION_1_ID = "test";
	private static final Email ASSOCIATION_1_EMAIL = new Email("test@example.com");
	private static final String ASSOCIATION_1_PASSWORD = "test_password";
	private static final String ASSOCIATION_1_NAME = "test_name";
	private static final String ASSOCIATION_1_ADDRESS = "test_address";
	private static final String ASSOCIATION_1_CODE = "test_code";

	private static final String ASSOCIATION_2_ID = "test";
	private static final Email ASSOCIATION_2_EMAIL = new Email("test2@example.com");
	private static final String ASSOCIATION_2_PASSWORD = "test2_password";
	private static final String ASSOCIATION_2_NAME = "test2_name";
	private static final String ASSOCIATION_2_ADDRESS = "test2_address";
	private static final String ASSOCIATION_2_CODE = "test2_code";

	@Mock
	private AssociationRepository associationRepository;

	@Mock
	private BoardgameRepository boardgameRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private GameTableRepository gameTableRepository;

	@Mock
	private AssociationMapper associationMapper;

	@Mock
	private BoardgameMapper boardgameMapper;

	@InjectMocks
	private AssociationServiceImpl associationService;

	@Test
	void getAllAssociationsShouldReturnTheSummariesWhenThereAreAssociations() {
		var association1 = new Association(
				new UserAccount(ASSOCIATION_1_EMAIL, ASSOCIATION_1_PASSWORD, ASSOCIATION_1_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_1_CODE, ASSOCIATION_1_ADDRESS);
		var association2 = new Association(
				new UserAccount(ASSOCIATION_2_EMAIL, ASSOCIATION_2_PASSWORD, ASSOCIATION_2_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_2_CODE, ASSOCIATION_2_ADDRESS);
		var summary1 = new AssociationSummary(ASSOCIATION_1_ID, ASSOCIATION_1_NAME, ASSOCIATION_1_ADDRESS);
		var summary2 = new AssociationSummary(ASSOCIATION_2_ID, ASSOCIATION_2_NAME, ASSOCIATION_2_ADDRESS);

		when(associationRepository.findAll()).thenReturn(List.of(association1, association2));
		when(associationMapper.toDto(association1))
				.thenReturn(new AssociationSummary(ASSOCIATION_1_ID, ASSOCIATION_1_NAME, ASSOCIATION_1_ADDRESS));
		when(associationMapper.toDto(association2))
				.thenReturn(new AssociationSummary(ASSOCIATION_2_ID, ASSOCIATION_2_NAME, ASSOCIATION_2_ADDRESS));
		List<AssociationSummary> summaries = associationService.getAssociations();

		assertThat(summaries).containsExactly(summary1, summary2);
		InOrder inOrder = inOrder(associationRepository, associationMapper);
		inOrder.verify(associationRepository).findAll();
		inOrder.verify(associationMapper).toDto(association1);
		inOrder.verify(associationMapper).toDto(association2);
		verifyNoInteractions(boardgameRepository);
	}

	@Test
	void getAssociationsByBoardgameIdShouldReturnSummariesWhenBoardgameExists() {
		String boardgameId = "bg-123";
		var association1 = new Association(
				new UserAccount(ASSOCIATION_1_EMAIL, ASSOCIATION_1_PASSWORD, ASSOCIATION_1_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_1_CODE, ASSOCIATION_1_ADDRESS);
		var summary1 = new AssociationSummary(ASSOCIATION_1_ID, ASSOCIATION_1_NAME, ASSOCIATION_1_ADDRESS);

		when(boardgameRepository.existsById(boardgameId)).thenReturn(true);
		when(associationRepository.findByBoardgamesId(boardgameId)).thenReturn(List.of(association1));
		when(associationMapper.toDto(association1)).thenReturn(summary1);

		List<AssociationSummary> summaries = associationService.getAssociations(boardgameId);

		assertThat(summaries).containsExactly(summary1);
		InOrder inOrder = inOrder(boardgameRepository, associationRepository, associationMapper);
		inOrder.verify(boardgameRepository).existsById(boardgameId);
		inOrder.verify(associationRepository).findByBoardgamesId(boardgameId);
		inOrder.verify(associationMapper).toDto(association1);
	}

	@Test
	void getAssociationsByBoardgameIdShouldThrowNotFoundWhenBoardgameDoesNotExist() {
		String invalidBoardgameId = "unknown-id";
		when(boardgameRepository.existsById(invalidBoardgameId)).thenReturn(false);

		assertThrows(BoardgameNotFoundException.class, () -> {
			associationService.getAssociations(invalidBoardgameId);
		});

		verify(boardgameRepository).existsById(invalidBoardgameId);
		verifyNoInteractions(associationRepository, associationMapper);
	}

	@Test
	void addBoardgamesToAssociationHappyPath() {
		List<String> boardgameIds = List.of("test1", "test2");
		var boardgame1 = new Boardgame("test1", 1, 2, 0, 0, "test_URL");
		var boardgame2 = new Boardgame("test1", 1, 2, 0, 0, "test_URL");
		var association1 = new Association(
				new UserAccount(ASSOCIATION_1_EMAIL, ASSOCIATION_1_PASSWORD, ASSOCIATION_1_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_1_CODE, ASSOCIATION_1_ADDRESS);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association1));
		when(boardgameRepository.findAllById(boardgameIds)).thenReturn(List.of(boardgame1, boardgame2));

		associationService.addBoardgamesToAssociation(boardgameIds, ASSOCIATION_1_EMAIL);

		assertThat(association1.getBoardgames()).contains(boardgame1, boardgame2);
		InOrder inOrder = inOrder(boardgameRepository, associationRepository);
		inOrder.verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		inOrder.verify(boardgameRepository).findAllById(boardgameIds);
		inOrder.verify(associationRepository).save(association1);
	}

	@Test
	void addBoardgamesToAssociationShouldThrowAssociationNotFoundExceptionWhenAssociationDoesNotExist() {
		List<String> boardgameIds = List.of("test1", "test2");
		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.empty());
		assertThrows(AssociationNotFoundException.class,
				() -> associationService.addBoardgamesToAssociation(boardgameIds, ASSOCIATION_1_EMAIL));
		verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		verifyNoMoreInteractions(associationRepository);
		verifyNoInteractions(boardgameRepository, associationMapper);
	}

	@Test
	void removeBoardgamesFromAssociationHappyPath() {
		String gameId = "game-1";
		List<String> boardgameIdsToRemove = List.of(gameId);
		var boardgame1 = new Boardgame("Game Name", 1, 4, 30, 60, "http://cover.url");
		var association1 = new Association(
				new UserAccount(ASSOCIATION_1_EMAIL, ASSOCIATION_1_PASSWORD, ASSOCIATION_1_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_1_CODE, ASSOCIATION_1_ADDRESS);
		boardgame1.setId(gameId);
		association1.getBoardgames().add(boardgame1);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association1));
		when(reservationRepository.existsByAssociationAndBoardgameIdAndStatus(association1, gameId,
				ReservationStatus.OPEN)).thenReturn(false);

		associationService.removeBoardgamesFromAssociation(boardgameIdsToRemove, ASSOCIATION_1_EMAIL);

		assertThat(association1.getBoardgames()).doesNotContain(boardgame1);
		InOrder inOrder = inOrder(reservationRepository, associationRepository);
		inOrder.verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		inOrder.verify(reservationRepository).existsByAssociationAndBoardgameIdAndStatus(association1, gameId,
				ReservationStatus.OPEN);
		inOrder.verify(associationRepository).save(association1);
	}

	@Test
	void removeBoardgamesFromAssociationShouldThrowAssociationNotFoundExceptionWhenAssociationDoesNotExist() {
		List<String> boardgameIds = List.of("test1");

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.empty());

		assertThrows(AssociationNotFoundException.class,
				() -> associationService.removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL));

		verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		verifyNoMoreInteractions(associationRepository);
		verifyNoInteractions(boardgameRepository, reservationRepository);
	}

	@Test
	void removeBoardgamesFromAssociationShouldThrowExceptionWhenGameHasOpenReservation() {
		String gameId = "game-1";
		List<String> boardgameIds = List.of(gameId);
		var game = new Boardgame("Game name", 1, 4, 60, 120, "url");
		var association1 = new Association(new UserAccount(ASSOCIATION_1_EMAIL, "pass", "name", UserRole.ASSOCIATION),
				"code", "addr");
		game.setId(gameId);
		association1.getBoardgames().add(game);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association1));
		when(reservationRepository.existsByAssociationAndBoardgameIdAndStatus(association1, gameId,
				ReservationStatus.OPEN)).thenReturn(true);

		assertThrows(BoardgameInUseException.class,
				() -> associationService.removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL));
		InOrder inOrder = inOrder(reservationRepository, associationRepository);
		inOrder.verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		inOrder.verify(reservationRepository).existsByAssociationAndBoardgameIdAndStatus(association1, gameId,
				ReservationStatus.OPEN);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void getAssociationBoardgamesReturnsBoardgameListWhenAssociationExists() {
		var boardgame1 = new Boardgame("Game 1", 1, 4, 30, 60, "url");
		var dto1 = new BoardgameDto("bg-1", "Game 1", 1, 4, 60, 10, "url");
		var association = new Association(new UserAccount(ASSOCIATION_1_EMAIL, "pass", "name", UserRole.ASSOCIATION),
				"code", "address");
		association.getBoardgames().add(boardgame1);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association));
		when(boardgameMapper.toDto(boardgame1)).thenReturn(dto1);

		List<BoardgameDto> result = associationService.getBoardgamesFrom(ASSOCIATION_1_EMAIL);

		assertThat(result).containsExactly(dto1);
		verify(boardgameMapper).toDto(boardgame1);
	}

	@Test
	void getAssociationBoardgamesThrowsExceptionWhenAssociationNotFound() {
		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.empty());
		assertThrows(AssociationNotFoundException.class,
				() -> associationService.getBoardgamesFrom(ASSOCIATION_1_EMAIL));
		verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
	}

	@Test
	void addTableToAssociationHappyPath() {
		var request = new GameTableRequest(4, GameTableSize.MEDIUM);
		var association = new Association(new UserAccount(ASSOCIATION_1_EMAIL, "pass", "name", UserRole.ASSOCIATION),
				ASSOCIATION_1_CODE, ASSOCIATION_1_ADDRESS);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association));

		associationService.addTableToAssociation(request, ASSOCIATION_1_EMAIL);

		assertThat(association.getGameTables()).hasSize(1);
		var addedTable = association.getGameTables().iterator().next();
		assertThat(addedTable.getCapacity()).isEqualTo(4);
		assertThat(addedTable.getSize()).isEqualTo(GameTableSize.MEDIUM);
		assertThat(addedTable.getAssociation()).isEqualTo(association);

		verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		verify(gameTableRepository).save(any(GameTable.class));
	}

	@Test
	void addTableToAssociationShouldThrowAssociationNotFoundExceptionWhenAssociationDoesNotExist() {
		var request = new GameTableRequest(4, GameTableSize.MEDIUM);
		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.empty());

		assertThrows(AssociationNotFoundException.class,
				() -> associationService.addTableToAssociation(request, ASSOCIATION_1_EMAIL));

		verify(associationRepository).findByAccountEmail(ASSOCIATION_1_EMAIL);
		verifyNoInteractions(gameTableRepository);
	}

	@Test
	void removeTableFromAssociationHappyPath() {
		String tableId = "table-123";
		var association = new Association(new UserAccount(ASSOCIATION_1_EMAIL, "pass", "name", UserRole.ASSOCIATION),
				"code", "addr");

		GameTable table = new GameTable();
		table.setId(tableId);
		table.setAssociation(association);
		association.addGameTable(table);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association));
		when(reservationRepository.existsByGameTableIdAndStatus(tableId, ReservationStatus.OPEN)).thenReturn(false);

		associationService.removeTableFromAssociation(tableId, ASSOCIATION_1_EMAIL);

		assertThat(association.getGameTables()).isEmpty();
		verify(associationRepository).save(association);
	}

	@Test
	void removeTableShouldThrowExceptionWhenTableHasOpenReservation() {
		String tableId = "table-123";
		var association = new Association(new UserAccount(ASSOCIATION_1_EMAIL, "pass", "name", UserRole.ASSOCIATION),
				"code", "addr");
		GameTable table = new GameTable();
		table.setId(tableId);
		association.addGameTable(table);

		when(associationRepository.findByAccountEmail(ASSOCIATION_1_EMAIL)).thenReturn(Optional.of(association));
		when(reservationRepository.existsByGameTableIdAndStatus(tableId, ReservationStatus.OPEN)).thenReturn(true);

		assertThrows(GameTableInUseException.class,
				() -> associationService.removeTableFromAssociation(tableId, ASSOCIATION_1_EMAIL));

		verify(associationRepository, never()).save(any());
	}

}
