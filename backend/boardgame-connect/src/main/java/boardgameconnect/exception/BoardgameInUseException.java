package boardgameconnect.exception;

public class BoardgameInUseException extends RuntimeException {

	private static final long serialVersionUID = -9124786326048198812L;

	public BoardgameInUseException(String message) {
		super(message);
	}
}
