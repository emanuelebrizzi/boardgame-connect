package boardgameconnect.dao;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanDatabase() {
	userRepository.deleteAll();
    }

    @Test
    void testFindByEmailWhenUserExistsReturnUser() {
	fail("Not yet implemented");
    }

}
