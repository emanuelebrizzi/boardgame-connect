package boardgameconnect.model;

public class Boardgame {
	
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
