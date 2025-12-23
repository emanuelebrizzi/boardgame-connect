package boardgameconnect.service;

import java.util.List;

import boardgameconnect.dto.ReservationSummary;

public interface ReservationService {

    public List<ReservationSummary> getAvailableReservations(String state, String game, String association);

}
