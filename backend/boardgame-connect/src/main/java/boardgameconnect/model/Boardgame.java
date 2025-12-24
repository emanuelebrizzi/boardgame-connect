package boardgameconnect.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "boardgames")
public class Boardgame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private int minPlayer;
    private int maxPlayer;
    private int minTimeInMin;
    private int timeInMinPerPlayer;

    public Boardgame(String name, int minPlayer, int maxPlayer, int minTimeInMin, int timeInMinPerPlayer) {
	super();
	this.name = name;
	this.minPlayer = minPlayer;
	this.maxPlayer = maxPlayer;
	this.minTimeInMin = minTimeInMin;
	this.timeInMinPerPlayer = timeInMinPerPlayer;
    }

    public String getName() {
	return name;
    }

    public int getMinPlayer() {
	return minPlayer;
    }

    public int getMaxPlayer() {
	return maxPlayer;
    }

    public int getMinTime() {
	return minTimeInMin;
    }

    public int getTimePerPlayer() {
	return timeInMinPerPlayer;
    }

}
