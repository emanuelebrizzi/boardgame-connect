package boardgameconnect.exception;

public class GameTableNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1797363425538469529L;

	public GameTableNotFoundException(String message) {
		super(message);
	}
}
