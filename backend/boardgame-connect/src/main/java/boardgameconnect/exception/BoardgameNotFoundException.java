package boardgameconnect.exception;

public class BoardgameNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1854084335127633460L;

	public BoardgameNotFoundException(String message) {
		super(message);
	}
}