package boardgameconnect.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import boardgameconnect.dto.PlayerSummary;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.dto.reservation.ReservationDetail;
import boardgameconnect.dto.reservation.ReservationSummary;
import boardgameconnect.model.Reservation;

@Component
public class ReservationMapper {
	public ReservationSummary mapToSummary(Reservation reservation) {
		List<String> participantIds = reservation.getPlayers().stream().map(p -> p.getId())
				.collect(Collectors.toList());
		return new ReservationSummary(reservation.getId(), reservation.getBoardgame().getName(),
				reservation.getBoardgame().getImagePath(), reservation.getAssociation().getAccount().getName(),
				reservation.getPlayers().size(), reservation.getMaxPlayers(), reservation.getStartTime(),
				reservation.getEndTime(), reservation.getStatus().name(), participantIds);
	}

	public ReservationDetail mapToDetail(Reservation reservation) {

		AssociationSummary assocSummary = new AssociationSummary(reservation.getAssociation().getId(),
				reservation.getAssociation().getAccount().getName(), reservation.getAssociation().getAddress());

		List<PlayerSummary> playerSummaries = reservation.getPlayers().stream()
				.map(p -> new PlayerSummary(p.getId(), p.getAccount().getName())).toList();

		return new ReservationDetail(reservation.getId(), reservation.getBoardgame().getName(),
				reservation.getBoardgame().getImagePath(), assocSummary, playerSummaries,
				reservation.getBoardgame().getMinTimeInMin(), reservation.getMaxPlayers(), reservation.getStartTime(),
				reservation.getEndTime(), reservation.getStatus().name());
	}

}