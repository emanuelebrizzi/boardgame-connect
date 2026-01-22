package boardgameconnect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import boardgameconnect.model.GameTable;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, String> {

	List<GameTable> findByAssociationId(String associationId);

}