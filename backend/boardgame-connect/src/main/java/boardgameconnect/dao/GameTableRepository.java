package boardgameconnect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import boardgameconnect.model.Association;
import boardgameconnect.model.GameTable;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, String> {

	List<GameTable> findByAssociationAndCapacityGreaterThanEqual(Association association, int capacity);

	@Query("SELECT gt FROM GameTable gt " + "WHERE gt.association.id = :associationId "
			+ "AND gt.capacity >= :minCapacity")
	List<GameTable> findSuitableTables(@Param("associationId") String associationId,
			@Param("minCapacity") int minCapacity);

}