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

import boardgameconnect.model.Association;
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

    @BeforeEach
    void setUp() {
	userAccountRepository.deleteAll();
	associationRepository.deleteAll();
    }

    @Test
    void shouldFindPlayerByUserAccount() {
	var email = new Email("info@ludoteca.com");
	var account = new UserAccount(email, "securePassword123", "Ludoteca Svelta", UserRole.ASSOCIATION);
	var association = new Association(account, "test_taxcode", "test_address");

	associationRepository.save(association);
	Optional<Association> result = associationRepository.findByAccount(account);

	assertThat(result.get()).isEqualTo(association);
    }

}
