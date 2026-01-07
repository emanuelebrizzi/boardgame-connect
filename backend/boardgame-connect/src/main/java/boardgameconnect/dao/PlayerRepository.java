package boardgameconnect.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;

public interface PlayerRepository extends JpaRepository<Player, String> {
	Optional<Player> findByAccount(UserAccount account);
}
