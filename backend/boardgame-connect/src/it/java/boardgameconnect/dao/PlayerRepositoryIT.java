package boardgameconnect.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@DataJpaTest
@Testcontainers
public class PlayerRepositoryIT {
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
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
	userAccountRepository.deleteAll();
	playerRepository.deleteAll();
    }

    @Test
    void shouldFindPlayerByUserAccount() {
	var email = new Email("player@boardgame.com");
	var account = new UserAccount(email, "securePassword123", "John Doe", UserRole.PLAYER);
	var player = new Player(account);
	playerRepository.save(player);

	Optional<Player> result = playerRepository.findByAccount(account);

	assertThat(result.get()).isEqualTo(player);
    }
}
