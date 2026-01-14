package boardgameconnect.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import boardgameconnect.model.Association;
import boardgameconnect.model.Reservation;
import boardgameconnect.model.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

	boolean existsByAssociationAndBoardgameIdAndStatus(Association association, String boardgameId,
			ReservationStatus status);
}
