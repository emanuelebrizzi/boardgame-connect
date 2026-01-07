package boardgameconnect.exception;

public class ForbiddenActionException extends RuntimeException {

	private static final long serialVersionUID = -6423109509965846124L;

	public ForbiddenActionException(String message) {
		super(message);
	}
}
