package boardgameconnect.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import boardgameconnect.model.Association;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanDatabase() {
	userRepository.deleteAll();
    }

    @Test
    void testFindByEmailWhenPlayerExistsReturnsUser() {
	var email = new Email("gamer@example.com");
	User user = new Player(email, "password", "test_name");

	userRepository.save(user);
	Optional<User> foundUser = userRepository.findByEmail(email);
	assertThat(foundUser).isPresent();

	Player foundPlayer = (Player) foundUser.get();
	assertThat(foundPlayer.getUsername()).isEqualTo("test_name");
	assertThat(foundPlayer.getEmail()).isEqualTo(email);
    }

    @Test
    void testFindByEmailWhenAssociationExistsReturnsUser() {
	var email = new Email("association@example.com");
	User user = new Association(email, "password", "test_name", "test_taxcode", "test_address");

	userRepository.save(user);
	Optional<User> foundUser = userRepository.findByEmail(email);
	assertThat(foundUser).isPresent();

	Association foundAssociation = (Association) foundUser.get();
	assertThat(foundAssociation.getName()).isEqualTo("test_name");
	assertThat(foundAssociation.getEmail()).isEqualTo(email);
	assertThat(foundAssociation.getTaxCode()).isEqualTo("test_taxcode");
	assertThat(foundAssociation.getAddress()).isEqualTo("test_address");
    }

}
