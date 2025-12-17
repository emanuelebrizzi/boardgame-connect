package boardgameconnect.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import boardgameconnect.model.Association;
import boardgameconnect.model.UserAccount;

public interface AssociationRepository extends JpaRepository<Association, String> {
    Optional<Association> findByAccount(UserAccount account);
}
