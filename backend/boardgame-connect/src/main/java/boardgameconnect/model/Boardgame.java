package boardgameconnect.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

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
	private String imagePath;

	public Boardgame() {
	}

	public Boardgame(String name, int minPlayer, int maxPlayer, int minTimeInMin, int timeInMinPerPlayer,
			String imagePath) {
		super();
		this.name = name;
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.minTimeInMin = minTimeInMin;
		this.timeInMinPerPlayer = timeInMinPerPlayer;
		this.imagePath = imagePath;
	}

	public String getName() {
		return name;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public int getTimePerPlayer() {
		return timeInMinPerPlayer;
	}

	public long calculateDuration(@Min(2) int maxPlayers) {
		return minTimeInMin + (maxPlayers * timeInMinPerPlayer);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public void setMinPlayer(int minPlayer) {
		this.minPlayer = minPlayer;
	}

	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public int getMinTimeInMin() {
		return minTimeInMin;
	}

	public void setMinTimeInMin(int minTimeInMin) {
		this.minTimeInMin = minTimeInMin;
	}

	public int getTimeInMinPerPlayer() {
		return timeInMinPerPlayer;
	}

	public void setTimeInMinPerPlayer(int timeInMinPerPlayer) {
		this.timeInMinPerPlayer = timeInMinPerPlayer;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
