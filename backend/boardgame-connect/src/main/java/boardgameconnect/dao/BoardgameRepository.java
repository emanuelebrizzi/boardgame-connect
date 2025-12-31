package boardgameconnect.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import boardgameconnect.model.Boardgame;

@Repository
public interface BoardgameRepository extends JpaRepository<Boardgame, String> {

    Optional<Boardgame> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}