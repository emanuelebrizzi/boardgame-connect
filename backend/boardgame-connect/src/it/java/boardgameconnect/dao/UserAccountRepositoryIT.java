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
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@DataJpaTest
@Testcontainers
class UserAccountRepositoryIT {

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

    @BeforeEach
    void setUp() {
	userAccountRepository.deleteAll();
    }

    @Test
    void shouldFindUserByEmail() {
	Email email = new Email("player@boardgame.com");
	UserAccount account = new UserAccount(email, "securePassword123", // In a real app, this should be encoded!
		"John Doe", UserRole.PLAYER);
	userAccountRepository.save(account);

	Optional<UserAccount> result = userAccountRepository.findByEmail(email);

	assertThat(result.get()).isEqualTo(account);
    }

}
