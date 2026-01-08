package boardgameconnect.exception;

public class PlayerAlreadyJoinedException extends RuntimeException {

	private static final long serialVersionUID = 4788256754487542700L;

	public PlayerAlreadyJoinedException(String message) {
		super(message);
	}
}