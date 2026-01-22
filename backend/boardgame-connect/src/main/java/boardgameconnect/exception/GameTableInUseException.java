package boardgameconnect.exception;

public class GameTableInUseException extends RuntimeException {

	private static final long serialVersionUID = 8000935849835126057L;

	public GameTableInUseException(String message) {
		super(message);
	}
}
