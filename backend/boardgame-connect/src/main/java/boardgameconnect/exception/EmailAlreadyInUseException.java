package boardgameconnect.exception;

public class EmailAlreadyInUseException extends RuntimeException {

	private static final long serialVersionUID = 8073903309318705099L;

	public EmailAlreadyInUseException(String message) {
		super(message);
	}

}
