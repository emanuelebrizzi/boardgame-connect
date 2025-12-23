package boardgameconnect.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToMany
    @JoinTable(name = "reservation_players", joinColumns = @JoinColumn(name = "reservation_id"), inverseJoinColumns = @JoinColumn(name = "player_id"))
    private List<Player> players = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;

    @ManyToOne
    @JoinColumn(name = "boardgame_id")
    private Boardgame boardgame;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant startTime;

    private Instant endTime;

    public Reservation() {
    }

    public Reservation(Player creator, Association association, Boardgame boardgame, Instant startTime,
	    Instant endTime) {
	this.players.add(creator);
	this.association = association;
	this.boardgame = boardgame;
	this.startTime = startTime;
	this.endTime = endTime;
	this.status = ReservationStatus.OPEN;
    }

    public String getId() {
	return id;
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

    public Instant getStartTime() {
	return startTime;
    }

    public Instant getEndTime() {
	return endTime;
    }
}