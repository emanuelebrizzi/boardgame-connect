package boardgameconnect.model;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
	private List<Player> players;
	private Association association;
	private Boardgame boardgame;
	private ReservationStatus status;
	
	public Reservation(Player players, Association association, Boardgame boardgame) {
		super();
		this.players = new ArrayList<Player>();
		this.players.add(players);
		this.association = association;
		this.boardgame = boardgame;
		this.status = ReservationStatus.OPEN;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Association getAssociation() {
		return association;
	}

	public Boardgame getBoardgame() {
		return boardgame;
	}

	public ReservationStatus getStatus() {
		return status;
	}
	
	
	
}