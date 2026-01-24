package boardgameconnect.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import boardgameconnect.model.Association;
import boardgameconnect.model.GameTable;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

	boolean existsByAssociationAndBoardgameIdAndStatus(Association association, String boardgameId,
			ReservationStatus status);

	boolean existsByGameTableIdAndStatus(String tableId, ReservationStatus status);

	@Query("SELECT r.gameTable FROM Reservation r " + "WHERE r.association.id = :associationId "
			+ "AND r.status = 'OPEN' " + "AND r.startTime < :requestedEnd " + "AND r.endTime > :requestedStart")
	List<GameTable> findOccupiedTables(@Param("associationId") String associationId,
			@Param("requestedStart") Instant requestedStart, @Param("requestedEnd") Instant requestedEnd);

}
