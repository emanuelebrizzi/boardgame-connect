package boardgameconnect.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import boardgameconnect.model.Association;
import boardgameconnect.model.Boardgame;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@DataJpaTest
@Testcontainers
class AssociationRepositoryIT {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private AssociationRepository associationRepository;

	@Autowired
	private BoardgameRepository boardgameRepository;

	private static final Email ASSOCIATION_1_EMAIL = new Email("test@example.com");
	private static final String ASSOCIATION_1_PASSWORD = "test_password";
	private static final String ASSOCIATION_1_NAME = "test_name";
	private static final String ASSOCIATION_1_ADDRESS = "test_address";
	private static final String ASSOCIATION_1_CODE = "test_code";

	private static final Email ASSOCIATION_2_EMAIL = new Email("test2@example.com");
	private static final String ASSOCIATION_2_PASSWORD = "test2_password";
	private static final String ASSOCIATION_2_NAME = "test2_name";
	private static final String ASSOCIATION_2_ADDRESS = "test2_address";
	private static final String ASSOCIATION_2_CODE = "test2_code";

	private Association association1;
	private Association association2;

	@BeforeEach
	void setUp() {
		associationRepository.deleteAll();
		userAccountRepository.deleteAll();
		boardgameRepository.deleteAll();

		association1 = new Association(
				new UserAccount(ASSOCIATION_1_EMAIL, ASSOCIATION_1_PASSWORD, ASSOCIATION_1_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_1_CODE, ASSOCIATION_1_ADDRESS);
		association2 = new Association(
				new UserAccount(ASSOCIATION_2_EMAIL, ASSOCIATION_2_PASSWORD, ASSOCIATION_2_NAME, UserRole.ASSOCIATION),
				ASSOCIATION_2_CODE, ASSOCIATION_2_ADDRESS);
		associationRepository.save(association1);
		associationRepository.save(association2);
	}

	@Test
	void findByAccountShouldReturnExistingAssociation() {
		UserAccount accountToFind = association1.getAccount();
		Optional<Association> result = associationRepository.findByAccount(accountToFind);
		assertThat(result.get()).isEqualTo(association1);
	}

	@Test
	void findByBoardgamesIdShouldReturnAssociationsOwningTheGame() {
		var boardgame = new Boardgame("Risiko", 3, 6, 60, 30);
		boardgameRepository.save(boardgame);
		association1.setBoardgames(new HashSet<>(Set.of(boardgame)));
		associationRepository.save(association1);
		var result = associationRepository.findByBoardgamesId(boardgame.getId());
		assertThat(result).hasSize(1);
		assertThat(result).containsExactly(association1);
	}
}