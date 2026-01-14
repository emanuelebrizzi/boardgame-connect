package boardgameconnect.service.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.mapper.AssociationMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class BoardgameAssociationServiceTest {

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
	private AssociationMapper associationMapper;

	@InjectMocks
	private BoardgameAssociationService associationService;

	@Test
	void testGetAllAssociationsShouldReturnTheSummariesWhenThereAreAssociations() {
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
	void testGetAssociationsByBoardgameIdShouldReturnSummariesWhenBoardgameExists() {
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
	void testGetAssociationsByBoardgameIdShouldThrowNotFoundWhenBoardgameDoesNotExist() {
		String invalidBoardgameId = "unknown-id";
		when(boardgameRepository.existsById(invalidBoardgameId)).thenReturn(false);

		assertThrows(BoardgameNotFoundException.class, () -> {
			associationService.getAssociations(invalidBoardgameId);
		});

		verify(boardgameRepository).existsById(invalidBoardgameId);
		verifyNoInteractions(associationRepository, associationMapper);
	}

}
