package boardgameconnect.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import boardgameconnect.model.GameTable;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, String> {

	List<GameTable> findByAssociationId(String associationId);

	@Query("""
			    SELECT t FROM GameTable t
			    WHERE t.association.id = :associationId
			    AND t.capacity >= :minCapacity
			""")
	List<GameTable> findAvailableTables(@Param("associationId") String associationId,
			@Param("minCapacity") int minCapacity, @Param("startTime") Instant startTime,
			@Param("endTime") Instant endTime);
}